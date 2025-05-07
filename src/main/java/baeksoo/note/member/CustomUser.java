package baeksoo.note.member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class CustomUser extends User {
    public String displayName;
    public String email;
    public Long id;

    CustomUser(String username, String password, List<GrantedAuthority> authorities){
        super(username, password, authorities);
    }
}
