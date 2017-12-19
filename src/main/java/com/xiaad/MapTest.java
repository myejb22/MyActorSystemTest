package com.xiaad;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p></p>
 *
 * @author Andy
 * @date 2017/12/19
 */
public class MapTest {

    public static void main(String[] args) {


        Heap h = new Heap();
        h.setId(1);
        h.setBlockAddress("测试块信息");

        Map<String,Heap> map = new HashMap(16);
        Set<Heap> set = new HashSet(16);
        map.put("heap1", h);
        set.add(h);

        System.out.println(map);

        Heap heap1 = map.get("heap1");
        heap1.setBlockAddress("test");

        System.out.println(map);
        System.out.println(set);
    }
}

class Heap{
    private int id;
    private String blockAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBlockAddress() {
        return blockAddress;
    }

    public void setBlockAddress(String blockAddress) {
        this.blockAddress = blockAddress;
    }

    @Override
    public String toString() {
        return "Heap{" +
                "id=" + id +
                ", blockAddress='" + blockAddress + '\'' +
                '}';
    }
}
