package com.imadcn.framework.idworker.registry.zookeeper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.imadcn.framework.idworker.config.ZookeeperConfigurarion;
import com.imadcn.framework.idworker.exception.RegExceptionHandler;
import com.imadcn.framework.idworker.registry.CoordinatorRegistryCenter;

/**
 * Zookeeper注册中心
 * 
 * @author yangchao
 * @since 2017-10-18
 */
public class ZookeeperRegistryCenter implements CoordinatorRegistryCenter {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<String, TreeCache> caches = new HashMap<>();

	private ZookeeperConfigurarion zkConfig;

	private CuratorFramework client;

	public ZookeeperRegistryCenter(ZookeeperConfigurarion zookeeperConfigurarion) {
		this.zkConfig = zookeeperConfigurarion;
	}

	public void init() {
		logger.debug("init zookeeper registry, connect to servers : {}", zkConfig.getServerLists());
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString(zkConfig.getServerLists()).retryPolicy(new ExponentialBackoffRetry(zkConfig.getBaseSleepTimeMilliseconds(), zkConfig.getMaxRetries(), zkConfig.getMaxSleepTimeMilliseconds())).namespace(zkConfig.getNamespace());
		if (0 != zkConfig.getSessionTimeoutMilliseconds()) {
			builder.sessionTimeoutMs(zkConfig.getSessionTimeoutMilliseconds());
		}
		if (0 != zkConfig.getConnectionTimeoutMilliseconds()) {
			builder.connectionTimeoutMs(zkConfig.getConnectionTimeoutMilliseconds());
		}
		if (zkConfig.getDigest() != null && !zkConfig.getDigest().isEmpty()) {
			builder.authorization("digest", zkConfig.getDigest().getBytes(StandardCharsets.UTF_8)).aclProvider(new ACLProvider() {

				public List<ACL> getDefaultAcl() {
					return ZooDefs.Ids.CREATOR_ALL_ACL;
				}

				public List<ACL> getAclForPath(final String path) {
					return ZooDefs.Ids.CREATOR_ALL_ACL;
				}
			});
		}
		client = builder.build();
		client.start();
		try {
			if (!client.blockUntilConnected(zkConfig.getMaxSleepTimeMilliseconds() * zkConfig.getMaxRetries(), TimeUnit.MILLISECONDS)) {
				client.close();
				throw new KeeperException.OperationTimeoutException();
			}
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
	}

	public void close() {
		for (Entry<String, TreeCache> each : caches.entrySet()) {
			each.getValue().close();
		}
		waitForCacheClose();
		CloseableUtils.closeQuietly(client);
	}

	/**
	 * TODO 等待500ms, cache先关闭再关闭client, 否则会抛异常 因为异步处理,
	 * 可能会导致client先关闭而cache还未关闭结束. 等待Curator新版本解决这个bug.
	 * BUG地址：https://issues.apache.org/jira/browse/CURATOR-157
	 */
	private void waitForCacheClose() {
		try {
			Thread.sleep(500L);
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	public String get(final String key) {
		TreeCache cache = findTreeCache(key);
		if (null == cache) {
			return getDirectly(key);
		}
		ChildData resultInCache = cache.getCurrentData(key);
		if (null != resultInCache) {
			return null == resultInCache.getData() ? null : new String(resultInCache.getData(), StandardCharsets.UTF_8);
		}
		return getDirectly(key);
	}

	private TreeCache findTreeCache(final String key) {
		for (Entry<String, TreeCache> entry : caches.entrySet()) {
			if (key.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	public String getDirectly(final String key) {
		try {
			return new String(client.getData().forPath(key), StandardCharsets.UTF_8);
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
			return null;
		}
	}

	public List<String> getChildrenKeys(final String key) {
		try {
			List<String> result = client.getChildren().forPath(key);
			Collections.sort(result, new Comparator<String>() {

				public int compare(final String o1, final String o2) {
					return o2.compareTo(o1);
				}
			});
			return result;
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
			return Collections.emptyList();
		}
	}

	public int getNumChildren(final String key) {
		try {
			Stat stat = client.checkExists().forPath(key);
			if (null != stat) {
				return stat.getNumChildren();
			}
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
		return 0;
	}

	public boolean isExisted(final String key) {
		try {
			return null != client.checkExists().forPath(key);
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
			return false;
		}
	}

	public void persist(final String key, final String value) {
		try {
			if (!isExisted(key)) {
				client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key, value.getBytes(StandardCharsets.UTF_8));
			} else {
				update(key, value);
			}
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
	}

	public void update(final String key, final String value) {
		try {
			client.inTransaction().check().forPath(key).and().setData().forPath(key, value.getBytes(StandardCharsets.UTF_8)).and().commit();
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
	}

	public void persistEphemeral(final String key, final String value) {
		try {
			if (isExisted(key)) {
				client.delete().deletingChildrenIfNeeded().forPath(key);
			}
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key, value.getBytes(StandardCharsets.UTF_8));
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
	}

	public String persistSequential(final String key, final String value) {
		try {
			return client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(key, value.getBytes(StandardCharsets.UTF_8));
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
		return null;
	}

	public void persistEphemeralSequential(final String key) {
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(key);
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
	}

	public void remove(final String key) {
		try {
			client.delete().deletingChildrenIfNeeded().forPath(key);
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
	}

	public long getRegistryCenterTime(final String key) {
		long result = 0L;
		try {
			persist(key, "");
			result = client.checkExists().forPath(key).getMtime();
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
		Preconditions.checkState(0L != result, "Cannot get registry center time.");
		return result;
	}

	public Object getRawClient() {
		return client;
	}

	public void addCacheData(final String cachePath) {
		TreeCache cache = new TreeCache(client, cachePath);
		try {
			cache.start();
			// CHECKSTYLE:OFF
		} catch (final Exception ex) {
			// CHECKSTYLE:ON
			RegExceptionHandler.handleException(ex);
		}
		caches.put(cachePath + "/", cache);
	}

	public void evictCacheData(final String cachePath) {
		TreeCache cache = caches.remove(cachePath + "/");
		if (null != cache) {
			cache.close();
		}
	}

	public Object getRawCache(final String cachePath) {
		return caches.get(cachePath + "/");
	}
}
