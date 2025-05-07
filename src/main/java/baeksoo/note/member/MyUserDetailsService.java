package baeksoo.note.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> result = memberRepository.findByUsername(username);
        if(result.isEmpty()){
            throw new UsernameNotFoundException("그런 아이디가 존재하지 않습니다.");
        }
        var user = result.get();

        List<GrantedAuthority> authority = new ArrayList<>();
        if(user.getUsername().equals("ryu2293")){
            authority.add(new SimpleGrantedAuthority("관리자"));
        }else{
            authority.add(new SimpleGrantedAuthority("일반유저"));
        }

        CustomUser customUser = new CustomUser(user.getUsername(), user.getPassword(), authority);
        customUser.displayName = user.getDisplayName();
        customUser.id = user.getId();
        customUser.email = user.getEmail();

        return customUser;
    }

}

