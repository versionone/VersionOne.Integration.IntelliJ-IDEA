/*(c) Copyright 2008, VersionOne, Inc. All rights reserved. (c)*/
package com.versionone.integration.idea;

enum ColunmnsNames {

    Title(0), ID(1), Parent(2), DetailEstimeate(3), Done(4), Effort(5), ToDo(6), Status(7);

    public static final int COUNT = 8;
    private int num;

    ColunmnsNames(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
