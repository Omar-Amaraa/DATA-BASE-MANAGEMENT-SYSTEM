import java.nio.ByteBuffer;

import org.example.Buffer;
import org.example.BufferManager;
import org.example.DBConfig;
import org.example.DiskManager;
import org.example.PageId;

public class BufferManagerTest {

    public static Buffer TestGetPage(PageId pageId, BufferManager bm) {
        return bm.getPage(pageId);
    }

    public static void TestsetCurrentReplacementPolicy(String policy, BufferManager bm) {
        bm.setCurrentReplacementPolicy(policy);

    }

    public static void TestFlushBuffers(Buffer buffer, BufferManager bm) {
        bm.FlushBuffers();
        System.out.println("Flush Page: " + buffer.getPageId());
    }
    public static void TestReplacementPolicy(BufferManager bm, DiskManager dm, DBConfig config) {
        // Allocate pages
        PageId pageId1 = dm.AllocPage();
        ByteBuffer buffer = ByteBuffer.allocate(config.getPagesize());
        buffer.put("Page1".getBytes());
        dm.WritePage(pageId1, buffer);


        PageId pageId2 = dm.AllocPage();
        buffer.clear();
        buffer.put("Page2".getBytes());
        dm.WritePage(pageId2, buffer);

        PageId pageId3 = dm.AllocPage();
        buffer.clear();
        buffer.put("Page3".getBytes());
        dm.WritePage(pageId3, buffer);


        // Get pages
        bm.getPage(pageId1);
        bm.getPage(pageId2);
        bm.getPage(pageId1);


        // Cette page doit etre remplacee
        bm.getPage(pageId3);



    }
    public static void main(String[] args) {
        DBConfig config = DBConfig.LoadDBConfig("./configDB.json");
        DiskManager dm = new DiskManager(config);
        BufferManager bm = new BufferManager(config, dm);
        TestReplacementPolicy(bm, dm,config);//Tester remplacement des pages
        bm.getBufferpool();
        //FlushBuffers
        PageId pageId = dm.AllocPage();
        ByteBuffer buffer = ByteBuffer.allocate(config.getPagesize());
        buffer.put("Page1".getBytes());
        dm.WritePage(pageId, buffer);
        Buffer buffer1 = bm.getPage(pageId);
        buffer1.setDirtyFlag(true);
        TestFlushBuffers(buffer1, bm);
        // Methode pour setCurrentReplacementPolicy
        // TestsetCurrentReplacementPolicy("MRU", bm);
        // TestReplacementPolicy(bm,dm,config);

        bm.getBufferpool();
    }
}