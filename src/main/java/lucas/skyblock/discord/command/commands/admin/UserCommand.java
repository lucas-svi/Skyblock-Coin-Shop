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
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import lucas.skyblock.utility.AppCtx;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UserCommand extends Command {

    public UserCommand(CommandData commandData, List<Role> roles, Type type, Consumer<net.dv8tion.jda.api.interactions.commands.Command> upsertionListener) {
        super(commandData, roles, type, upsertionListener);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event, User usr) {
        net.dv8tion.jda.api.entities.User usr1 = event.getOptions().get(0).getAsUser();

        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        User user = userRepository.findByDisordID(usr1.getId());

        if(user == null){
            event.replyEmbeds(embed("User not found!", null, RED)).setEphemeral(event.getChannelType() != ChannelType.PRIVATE).queue();
            return;
        }

        user.setUsername(usr1.getName());
        userRepository.save(user);

        String formatted = String.format(
                        "Username: %s\n" +
                        "Role: `%s`\n" +
                        "Discord ID: `%s`\n" +
                        "Balance: `%s credits`\n" +
                        "Coins bought: `WIP`",
                usr1.getName(),
                user.getRole().getFancyName(),
                usr1.getId(),
                user.getBalance()
        );

        event.replyEmbeds(embed(formatted,"User found", YELLOW)).setEphemeral(event.getChannelType() != ChannelType.PRIVATE)
                .addComponents(
                        ActionRow.of(
                                Button.of(ButtonStyle.DANGER, "user_ban_" + user.getDisordID(), "Ban this user"),
                                Button.of(ButtonStyle.PRIMARY, "user_role_" + user.getDisordID(), "Modify role"),
                                Button.of(ButtonStyle.DANGER, "user_delete_" + user.getDisordID(), "Delete this user")
                        )
                )
                .queue();
    }

    @Override
    public void buttonClicked(ButtonInteractionEvent event, User user, String id) {
        if(id.startsWith("user_ban")){
            banUser(event, id.split("_")[2]);
        }else if(id.startsWith("user_role")){
            modifyRoleSelectMenu(event, id.split("_")[2]);
        }else if(id.startsWith("user_delete")){
            deleteUser(event, id.split("_")[2]);
        }
    }

    @Override
    public void modalInteractied(ModalInteractionEvent event, User user, String id) {

    }

    @Override
    public void selectMenuInteracted(GenericSelectMenuInteractionEvent event, User user, String id) {
        if(id.startsWith("user_role_selection")){
            modifyRole(event, id.split("_")[3]);
        }
    }

    private void deleteUser(ButtonInteractionEvent event, String id){
        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        User user = userRepository.findByDisordID(id);

        userRepository.delete(user);

        event.replyEmbeds(embed("Deleted user " + user.getDisordID(), "Done", YELLOW)).setEphemeral(event.getChannelType() != ChannelType.PRIVATE).queue();
    }

    private void banUser(ButtonInteractionEvent event, String id){
        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        User user = userRepository.findByDisordID(id);

        user.setRole(Role.BANNED);

        userRepository.save(user);

        event.replyEmbeds(embed("Banned user " + user.getDisordID(), "Done", YELLOW)).setEphemeral(event.getChannelType() != ChannelType.PRIVATE).queue();
    }

    private void modifyRole(GenericSelectMenuInteractionEvent event, String id){
        String selection = (String) event.getValues().get(0);

        Role role = Role.valueOf(selection);

        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        User user = userRepository.findByDisordID(id);

        user.setRole(role);

        userRepository.save(user);

        event.replyEmbeds(embed("Updated `" + user.getDisordID() + "` role to `" + role.getFancyName() + "`", null, YELLOW)).setEphemeral(event.getChannelType() != ChannelType.PRIVATE).queue();
    }

    private void modifyRoleSelectMenu(ButtonInteractionEvent event, String id){
        StringSelectMenu.Builder roleSelection = StringSelectMenu.create("user_role_selection_" + id);

        UserRepository userRepository = AppCtx.context().getBean(UserRepository.class);

        User user = userRepository.findByDisordID(id);

        List<Role> roles = Arrays.stream(Role.values()).collect(Collectors.toList());

        roles.remove(user.getRole());

        for (Role role : roles) {
            roleSelection.addOption(role.getFancyName(), role.name());
        }

        event.replyEmbeds(
                embed("Please select a new role", null, YELLOW)
        ).addActionRow(
                roleSelection.build()
        ).setEphemeral(
                event.getChannelType() != ChannelType.PRIVATE
        ).queue();
    }
}
