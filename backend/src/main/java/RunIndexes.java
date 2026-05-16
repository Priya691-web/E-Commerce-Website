import com.fashionstore.util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class RunIndexes {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {
             
            System.out.println("Adding idx_orders_user_id...");
            try {
                stmt.execute("CREATE INDEX idx_orders_user_id ON orders(user_id)");
                System.out.println("Success!");
            } catch (Exception e) {
                System.out.println("Warning: " + e.getMessage());
            }

            System.out.println("Adding idx_products_category_id...");
            try {
                stmt.execute("CREATE INDEX idx_products_category_id ON products(category_id)");
                System.out.println("Success!");
            } catch (Exception e) {
                System.out.println("Warning: " + e.getMessage());
            }
            
            System.out.println("All done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
