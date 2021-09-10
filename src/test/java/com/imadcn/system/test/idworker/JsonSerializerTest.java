package com.imadcn.system.test.idworker;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.imadcn.framework.idworker.register.zookeeper.NodeInfo;
import com.imadcn.framework.idworker.serialize.json.FastJsonSerializer;
import com.imadcn.framework.idworker.serialize.json.JacksonSerializer;
import com.imadcn.system.test.ResultPrinter;

public class JsonSerializerTest extends ResultPrinter {

    private FastJsonSerializer<NodeInfo> fastJsonSerializer;
    private JacksonSerializer<NodeInfo> jacksonSerializer;
    private NodeInfo nodeInfo;

    @Test
    public void testFastjsonSerializer() throws Exception {
        String json = fastJsonSerializer.toJsonString(nodeInfo);
        rawPrint(json);

        NodeInfo fromJson = fastJsonSerializer.parseObject(json, NodeInfo.class);
        print(fromJson);
        System.out.println(fromJson);
    }

    @Test
    public void testJacksonSerializer() throws Exception {
        String json = jacksonSerializer.toJsonString(nodeInfo);
        rawPrint(json);

        NodeInfo fromJson = jacksonSerializer.parseObject(json, NodeInfo.class);
        print(fromJson);
        System.out.println(fromJson.toString());
    }

    @Test
    public void testfromFastJsontoJackson() throws Exception {
        String json = fastJsonSerializer.toJsonString(nodeInfo);
        NodeInfo jsonNodeInfo = jacksonSerializer.parseObject(json, NodeInfo.class);

        rawPrint(equals(nodeInfo, jsonNodeInfo));
        System.out.println(jsonNodeInfo);
    }

    @Test
    public void testfromJacksontoFastJson() throws Exception {
        String json = jacksonSerializer.toJsonString(nodeInfo);
        NodeInfo jsonNodeInfo = fastJsonSerializer.parseObject(json, NodeInfo.class);

        rawPrint(equals(nodeInfo, jsonNodeInfo));
        System.out.println(jsonNodeInfo.toString());
    }

    @Before
    public void gensrc() {
        nodeInfo = new NodeInfo();
        nodeInfo.setCreateTime(new Date());
        nodeInfo.setGroupName("gn");
        nodeInfo.setHostName("hostname");
        nodeInfo.setIp("127.0.0.1");
        nodeInfo.setNodeId("nodeid");
        nodeInfo.setUpdateTime(new Date());
        nodeInfo.setWorkerId(102);

        fastJsonSerializer = new FastJsonSerializer<>();
        jacksonSerializer = new JacksonSerializer<>();

    }

    public boolean equals(NodeInfo n1, NodeInfo n2) {
        // NodeId、IP、HostName、GroupName 相等（本地缓存==ZK数据）
        if (!n1.getNodeId().equals(n2.getNodeId())) {
            return false;
        }
        if (!n1.getIp().equals(n2.getIp())) {
            return false;
        }
        if (!n1.getHostName().equals(n2.getHostName())) {
            return false;
        }
        if (!n1.getGroupName().equals(n2.getGroupName())) {
            return false;
        }
        return true;
    }
}
