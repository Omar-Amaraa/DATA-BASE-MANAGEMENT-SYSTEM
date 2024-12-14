


import java.nio.ByteBuffer;

import org.example.DBConfig;
import org.example.DiskManager;
import org.example.PageId;

public class DiskManagerTests {

    public static PageId TestAllocPage(DiskManager dm) {
        PageId pageId = dm.AllocPage();
        System.out.println("AllocPage: " + pageId);
        return pageId;
    }

    public static void TestDeallocPage(PageId pageId, DiskManager dm) {

        dm.DeallocPage(pageId);
        System.out.println("DeallocPage: " + pageId);
    }

    public static void TestSaveState(DiskManager dm) {
        dm.SaveState();
        System.out.println("SaveState.");
    }
    public static void TestWritePage(PageId pageId, ByteBuffer buffer, DiskManager dm) {
        dm.WritePage(pageId, buffer);
        System.out.println("Write Page: " + pageId);
    }

    public static void TestReadPage(PageId pageId, ByteBuffer buffer, DiskManager dm) {
        buffer.clear();
        int bytesRead = dm.ReadPage(pageId, buffer);
        if (bytesRead > 0) {
//            System.out.println("Page read: " + pageId);
//            System.out.println("Buffer limit: " + buffer.limit());
//            System.out.println("Buffer capacity: " + buffer.capacity());
//            System.out.println("Buffer position: " + buffer.position());
       } else {
            System.out.println("La page ne peut pas être lu: " + pageId);
        }
    }


    public static void TestLoadState(DiskManager dm) {

        // Charge et affiche l'état
        dm.LoadState();
        System.out.println("LoadSate. Free pages: " + dm.getFreePages());
    }
    public static void main(String[] args) {
        DBConfig config = DBConfig.LoadDBConfig("./files/dataset_1.json");
        DiskManager dm = new DiskManager(config);

        // allouer une page
        PageId pid = TestAllocPage(dm);

        // creer un buffer d'ecriture
        ByteBuffer writeBuffer = ByteBuffer.allocate(config.getPagesize());
        byte[] data = "Lorem Ipsum is simply een the industry's standard dummy text ever sincescrambled it tnly five centurieronic typesetting, remaining essentially unchanged.".getBytes();
        writeBuffer.put(data);

        // ecrire des donnees dans la page
        TestWritePage(pid, writeBuffer, dm);

        // creer un buffer de lecture et lire les donnees de la page
        ByteBuffer readBuffer = ByteBuffer.allocate(config.getPagesize());
        TestReadPage(pid, readBuffer, dm);

        // print donnees lues
        // readBuffer.flip();
        // byte[] readData = new byte[readBuffer.remaining()];
        // readBuffer.get(readData);
        // System.out.println("Read data: " + new String(readData, StandardCharsets.UTF_8));

        // // allouer une autre page
        // PageId pid1 = TestAllocPage(dm);

        // // test sauvegarde et chargement de l'etat avant deallocation
        // TestSaveState(dm);
        // TestLoadState(dm);

        // // deallocation de la page
        // TestDeallocPage(pid1, dm);
        // // test sauvegarde et chargement de l'etat apres deallocation
        // TestSaveState(dm);
        // TestLoadState(dm);


        }

}