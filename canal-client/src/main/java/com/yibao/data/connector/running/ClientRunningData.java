package com.yibao.data.connector.running;

/**
 * @author liuwenyi
 * @date 2019/11/27
 */
public class ClientRunningData {

    private String groupId;
    private String address;
    private boolean active = true;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
