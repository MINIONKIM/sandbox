package com.neenano;

public class Main {

    public static void main(String[] args) {

        StackAPI os = new StackAPI();

        os.getNovaInfo();
        //os.createVolume();
        os.createInstance("instance3-cent7","2",os.createVolume("test",20,"test","3f62eb8f-e90c-48bb-8e61-b4c4bb71c16b"));
    }
}
