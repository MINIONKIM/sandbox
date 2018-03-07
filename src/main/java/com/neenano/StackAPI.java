package com.neenano;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.storage.BlockVolumeService;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.openstack4j.model.compute.builder.BlockDeviceMappingBuilder;
import org.openstack4j.model.image.Image;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeSnapshot;
import org.openstack4j.model.storage.block.VolumeType;
import org.openstack4j.openstack.OSFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StackAPI {

    private static OSClientV3 os;

    private final Identifier domainIdentifier = Identifier.byId("default");
    private final String osUrl = "http://172.30.0.2:5000/v3";
    private final String id = "minion";
    private final String pw = "c3ntr!x5-K";

    /**
     * Constructor : performs authorization
     */
    StackAPI() {
        os = OSFactory.builderV3()
                .endpoint(osUrl)
                .credentials(id, pw, domainIdentifier)
                .authenticate();
    }

    /**
     * getNovaInfo() : print info -- hardcoded
     */
    public void getNovaInfo() {
        List<? extends Image> images = os.images().list();
        List<? extends Network> networks = os.networking().network().list();
        List<? extends Volume> volumes = os.blockStorage().volumes().list();
        List<? extends VolumeType> types = os.blockStorage().volumes().listVolumeTypes();
        List<? extends VolumeSnapshot> snapshots = os.blockStorage().snapshots().list();

        System.out.println("\n\nGLANCE IMAGES >>");
        System.out.println(images.toString());
        System.out.println("\n\nNETWORKS >>");
        System.out.println(networks.toString());
        System.out.println("\n\nVOLUMES >>");
        System.out.println(types.toString());
        System.out.println("\n\nVOLUME TYPES >>");
        System.out.println(volumes.toString());
        System.out.println("\n\nSNAPSHOTS >>");
        System.out.println(snapshots.toString());
    }

    /**
     * createVolume() : create a volume using snapshot image
     * @return : Volume ID of created one.
     */
    public String createVolume(String volumeName, int size, String description, String snapshotId) {

        try{
            Volume v = os.blockStorage().volumes()
                    .create(Builders.volume()
                            .name(volumeName)
                            .size(size)
                            .description(description)
                            .snapshot(snapshotId)
                            .bootable(true)
                            .build()
                    );

            System.out.println("new volume id : " + v.getId());

            return v.getId();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createVolume1(int size) {

        try{
            Volume v = os.blockStorage().volumes()
                    .create(Builders.volume()
                            .size(20)
                            .bootable(true)
                            .build()
                    );

            System.out.println("new volume id : " + v.getId());

            return v.getId();

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

    public String takeSnapshot(String serverId) {
        return "";
    }

    public void addFlavor(int vCPU, int vMem, int vHDD) {

    }

    /**
     * createInstance() : create new instance -- hardcoded
     */
    public Server createInstance(String name, String flavor, String volumeId) {

        ServerCreateBuilder builder = null;
        ServerCreate sc = null;
        List<String> networks = new ArrayList<String>();
        Map<String, String> metadata = new HashMap<String, String>();

        networks.add("af74d82f-40e0-483e-92ee-7090b9140a99"); // internal
        metadata.put("BOOT_FROM_VOLUME", "true");
        metadata.put("VOLUME_ID", volumeId);

        try {
            TimeUnit.SECONDS.sleep(5);

            builder = Builders.server()
                    .name(name)
                    .flavor(flavor)
                    .addMetadata(metadata)
                    .networks(networks)
                    .addPersonality("/etc/motd", "Welcome to the new VM! Restricted access only");

            BlockDeviceMappingBuilder blockDeviceMappingBuilder = Builders.blockDeviceMapping()
                    .uuid(volumeId)
                    //.deviceName("/dev/vda")
                    .diskBus("ide")
                    .bootIndex(0)
                    .deleteOnTermination(true);

            builder.blockDevice(blockDeviceMappingBuilder.build());
            sc = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Boot the Server
        Server server = os.compute().servers().boot(sc);

        System.out.println("Successfully created : " + server.getId());

        //IP도 같이 리턴해주기

        return server;
    }

    public void deleteInstance(String serverId) {

    }

    public void turnOnVM(String serverId) {
        // Server server = os.compute().servers().get("serverId");

    }

    public void turnOffVM(String serverId) {

    }

    public void resetVM(String serverId) {

    }

    public String getFloatingIPOfVM(String serverId) {

        return "";
    }

}