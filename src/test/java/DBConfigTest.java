import org.example.DBConfig;

public class DBConfigTest {
    public static void main(String[] args) {
        DBConfig config = DBConfig.LoadDBConfig("./configDB.json");
        System.out.println(config.getPagesize());
        System.out.println(config.getDm_maxfilesize());
        System.out.println(config.getBm_buffercount());
        System.out.println(config.getBm_policy());
        

        }
    
    }
       