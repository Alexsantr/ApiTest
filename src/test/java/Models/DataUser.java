package Models;

import lombok.Data;

@Data
public class DataUser {
    private int id;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar;
}
