package lucas.skyblock.mongo.entity.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lucas.skyblock.mongo.repository.UserRepository;
import lucas.skyblock.utility.AppCtx;

@Document(collection = "users")
@Builder
@Getter
@Setter
public class User{

    @Id
    private String id;

    private String disordID;
    private String username;

    private Role role;

    private Double balance, onHold, withdrawableBalance;

    private Boolean acceptingOffers;

    public double getWithdrawableBalance(){
        if(withdrawableBalance == null){
            return 0;
        }

        return withdrawableBalance;
    }

    public static User findOrCreate(GenericInteractionCreateEvent event){
        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        User user = userRepository.findByDisordID(event.getUser().getId());

        if(user == null){
            user = User.builder()
                    .disordID(event.getUser().getId())
                    .acceptingOffers(false)
                    .role(Role.EVERYONE)
                    .username(event.getUser().getName())
                    .balance(0.0D)
                    .onHold(0.0D)
                    .withdrawableBalance(0.0D)
                    .build();

            userRepository.save(user);
        }

        return user;
    }

    public static User findOrCreate(net.dv8tion.jda.api.entities.User usr){
        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        User user = userRepository.findByDisordID(usr.getId());

        if(user == null){
            user = User.builder()
                    .disordID(usr.getId())
                    .acceptingOffers(false)
                    .username(usr.getName())
                    .role(Role.EVERYONE)
                    .balance(0.0D)
                    .onHold(0.0D)
                    .withdrawableBalance(0.0D)
                    .build();

            userRepository.save(user);
        }

        return user;
    }

}
