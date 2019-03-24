package com.c2p.dinner.System;

import java.io.Serializable;

class ReasonObject implements Serializable {
    String name;
    int id;
    int number;
    
    
    ReasonObject(String name, int id, int number) {
        this.name = name;
        this.id = id;
        this.number = number;
    }
    
}
