package edu.dev.entityLinking;

import java.util.ArrayList;

/**
 * Created by zxy on 2016/7/12.
 * 存储问题中实体链接中内容
 */
public class LinkInfo {
    private String entityConten = "";
    /**Quote,Book,UserDefine*/
    private String entityClass = "";
    /**Q,A,B,C,D*/
    private String entityLocation = "";
    private ArrayList<Integer> entityRetrivalList= new ArrayList<>();

    public boolean setEntityConten(String input) {
        entityConten = input;
        if (entityConten == null) {
            return false;
        }
        return true;
    }

    public String getEntityConten() {
        return entityConten;
    }

    public boolean setEntityClass(String input) {
        entityClass = input;
        if (entityClass == null) {
            return false;
        }
        return true;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public boolean setEntityLocation(String input) {
        entityLocation = input;
        if (entityLocation == null) {
            return false;
        }
        return true;
    }

    public String getEntityLocation() {
        return entityLocation;
    }

    public boolean setEntityRetrivalList(ArrayList<Integer> docID) {
        entityRetrivalList = docID;
        if (entityRetrivalList != null) {
            return true;
        }
        return false;
    }
    public ArrayList<Integer> getEntityRetrivalList() {
        return entityRetrivalList;
    }
}
