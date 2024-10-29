public interface SearchInterface {
    public User[] searchParameter(String parameter); //can be used for any of the fields in createProfile
    public User[] exactMatch(String bedTime, boolean alcohol, boolean smoke, boolean guests, int tidy, int roomHours);
    public User[] partialMatch(String bedTime, boolean alcohol, boolean smoke, boolean guests, int tidy, int roomHours);
}
