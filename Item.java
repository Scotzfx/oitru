    package Main;

    import java.util.Date;
import javafx.scene.image.Image;

    public class Item {

        private int itemID;
        private String itemName;
        private String description;
        private String location;
        private Image image;
        private String status;
        private String UserName;
        private Date dateSubmitted;
        private Date dateClaimed;
        private String category;

        // Constructor
        public Item(int itemID, String itemName, String description, String location, Image image, String status,
                    String UserName, Date dateSubmitted, Date dateClaimed, String category) {
            this.itemID = itemID;
            this.itemName = itemName;
            this.description = description;
            this.location = location;
            this.image = image;
            this.status = status;
            this.UserName = UserName;
            this.dateSubmitted = dateSubmitted;
            this.dateClaimed = dateClaimed;
            this.category = category;
        }
        
       
        // Getters and Setters
        public int getItemID() {
            return itemID;
        }

        public void setItemID(int itemID) {
            this.itemID = itemID;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            this.UserName = userName;
        }

        public Date getDateSubmitted() {
            return dateSubmitted;
        }

        public void setDateSubmitted(Date dateSubmitted) {
            this.dateSubmitted = dateSubmitted;
        }

        public Date getDateClaimed() {
            return dateClaimed;
        }

        public void setDateClaimed(Date dateClaimed) {
            this.dateClaimed = dateClaimed;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
        private String reporterName;

        public String getReporterName() {
            return reporterName;
        }

        public void setReporterName(String reporterName) {
            this.reporterName = reporterName;
        }
    }
