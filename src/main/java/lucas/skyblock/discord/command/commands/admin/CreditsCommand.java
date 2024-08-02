package lucas.skyblock.discord.command.commands.admin;

import lucas.skyblock.discord.command.Command;
import lucas.skyblock.mongo.entity.user.Role;
import lucas.skyblock.mongo.entity.user.User;
import lucas.skyblock.mongo.repository.UserRepository;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import lucas.skyblock.utility.AppCtx;

import java.util.List;
import java.util.function.Consumer;

public class CreditsCommand extends Command {

    public CreditsCommand(CommandData commandData, List<Role> roles, Type type, Consumer<net.dv8tion.jda.api.interactions.commands.Command> upsertionListener) {
        super(commandData, roles, type, upsertionListener);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, User usr) {
        event.deferReply().queue();

        if(event.getOptions().size() != 2){
            event.getHook().sendMessageEmbeds(embed("Wrong usage", null, RED)).setEphemeral(true).queue();
            return;
        }

        net.dv8tion.jda.api.entities.User user = event.getOptions().get(0).getAsUser();
        double amount = event.getOptions().get(1).getAsDouble();

        UserRepository customerRepository = AppCtx.context().getBean(UserRepository.class);

        User buyer = customerRepository.findByDisordID(user.getId());

        if(buyer == null){
            User user1 = User.findOrCreate(user);

            user1.setBalance(amount);

            customerRepository.save(user1);
        }else{
            buyer.setBalance(buyer.getBalance() + amount);
            customerRepository.save(buyer);
        }

        event.getHook().sendMessageEmbeds(embed(String.format("`%.2f` credits were added to %s's account", amount, user.getName()), "Success", GREEN))
                .setEphemeral(event.getChannelType() != ChannelType.PRIVATE).queue();
    }

    @Override
    public void buttonClicked(ButtonInteractionEvent event, User user, String id) {

    }

    @Override
    public void modalInteractied(ModalInteractionEvent event, User user, String id) {

    }

    @Override
    public void selectMenuInteracted(GenericSelectMenuInteractionEvent event, User user, String id) {

    }
}
