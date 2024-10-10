package org.example;


public class DiskManagerTests {
    private DBConfig dbConfiginstance;

    public static PageId TestAllocPage() {
        DBConfig config = DBConfig.LoadDBConfig("./files/dataset_1.json");
        DiskManager dm = new DiskManager(config, config.getDbpath());

        // Allocate a page
        PageId pageId = dm.AllocPage();
        System.out.println("Allocated Page: " + pageId);
        return pageId;
    }

    public static void TestDeallocPage(PageId pageId) {
        DBConfig config = DBConfig.LoadDBConfig("./files/dataset_1.json");
        DiskManager dm = new DiskManager(config, config.getDbpath());

        // Deallocate une page
        dm.DeallocPage(pageId);
        System.out.println("Deallocated Page: " + pageId);
    }

    public static void TestSaveState() {
        DBConfig config = DBConfig.LoadDBConfig("./files/dataset_1.json");
        DiskManager dm = new DiskManager(config, config.getDbpath());

        // Allocate a page and save state
        dm.SaveState();
        System.out.println("State saved.");
    }

    public static void TestLoadState() {
        DBConfig config = DBConfig.LoadDBConfig("./files/dataset_1.json");
        DiskManager dm = new DiskManager(config, config.getDbpath());

        // Charge et affiche l'état
        dm.LoadState();
        System.out.println("State loaded. Free pages: " + dm.getFreePages());
    }
    public static void main(String[] args) {
        TestLoadState();
        PageId pid= TestAllocPage();
        TestLoadState();
        TestSaveState();
        TestDeallocPage(pid);
        TestSaveState();
        TestLoadState();

    }
}