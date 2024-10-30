public interface Profile { //boolean return type to check for success
    public boolean createProfile(String name, String pwd, String email, String phoneNum, String userDesc, String uni); //potentially adding "photo" field
    public boolean deleteProfile(); //deletes user object
    public String getName(); //getters
    public String getPwd();
    public String getEmail();
    public String getPhoneNum();
    public String getDesc();
    public String getUni();
    public String getPreferences(); //convert data from preferences fields into a string
    public void setName(String newName); //setters
    public void setPwd(String newPwd);
    public void setEmail(String newEmail);
    public void setPhoneNum(String newPhoneNum);
    public void setDesc(String newDesc);
    public void setUni(String newUni);
    public void setPreferences(String bedTime, boolean alcohol, boolean smoke, boolean guests, int tidy, int roomHours); //I figured the boolean data could be true/false questions, and we can measure tidiness on a scale 1-1// 0?
}