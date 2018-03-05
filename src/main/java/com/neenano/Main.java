package com.neenano;

import org.openstack4j.model.compute.Image;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        StackWrapper os = new StackWrapper();

        os.getNovaInfo();
        //os.createVolume();
    }
}
