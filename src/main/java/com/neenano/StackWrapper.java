package com.neenano;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.openstack4j.model.compute.builder.BlockDeviceMappingBuilder;
import org.openstack4j.model.image.Image;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.openstack.OSFactory;

import java.util.List;

public class StackWrapper {

    private static OSClientV2 os;
    private final String osUrl = "http://172.30.0.2:5000/v2.0";
    private final String id = "admin";
    private final String pw = "robot123!";

    /**
     * Constructor : performs authorization
     */
    StackWrapper() {
            os = OSFactory.builderV2()
                .endpoint(osUrl)
                .credentials(id,pw)
                .tenantName("admin")
                .authenticate();
    }

    /**
     * getNovaInfo() : print info -- hardcoded
     */
    public void getNovaInfo() {
        List<? extends Image> images = os.images().list();
        List<? extends Network> networks = os.networking().network().list();
        List<? extends Volume> volumes = os.blockStorage().volumes().list();

        System.out.println("\n\nGLANCE IMAGES >>");
        System.out.println(images.toString());
        System.out.println("\n\nNETWORKS >>");
        System.out.println(networks.toString());
        System.out.println("\n\nVOLUMES >>");
        System.out.println(volumes.toString());
    }

    /**
     * createVolume() : create a volume using snapshot image --- hardcoded
     */
    public Volume createVolume() {

        String volumeName = "test";
        String description = "test";

        /* imageId of 'centos7-snapshot-initial' */
        String imageId = "440cf866-947e-4f51-a47d-c0423475e059";

        try{
            Volume v = os.blockStorage().volumes()
                    .create(Builders.volume()
                            .name(volumeName)
                            .description(description)
                            .imageRef(imageId)
                            .bootable(true)
                            .build()
                    );

            System.out.println("new volume id : " + v.getId());
            return v;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * deleteVolume() : to be implemented
     * @param volumeId
     */
    public void deleteVolume(String volumeId){

    }


    /**
     * createInstance() : create new instance -- hardcoded
     */
    public Server createInstance() {

        Volume v = createVolume();

        BlockDeviceMappingBuilder blockDeviceMappingBuilder = Builders.blockDeviceMapping()
                .uuid(v.getId())
                .deviceName("/dev/vda")
                .bootIndex(0);

        /*
        ServerCreate sc = Builders.server()
                            .name("Server")
                            .blockDevice(blockDeviceMappingBuilder.build());

        Server server = os.compute().servers().boot(sc);
        */


        // Create a Server Model Object
        ServerCreate sc = Builders.server()
                .name("TEST")
                .flavor("2")
                .blockDevice(blockDeviceMappingBuilder.build())
                .addPersonality("/etc/motd", "Welcome to the new VM! Restricted access only")
                .build();

        // Boot the Server
        Server server = os.compute().servers().boot(sc);

        return server;
    }

}
