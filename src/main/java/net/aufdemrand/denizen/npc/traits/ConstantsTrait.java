package net.aufdemrand.denizen.npc.traits;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.notables.Notable;
import net.aufdemrand.denizen.scripts.helpers.ScriptHelper;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.command.exception.CommandException;
import net.citizensnpcs.util.Messages;
import net.citizensnpcs.util.Paginator;


public class ConstantsTrait extends Trait {

	private Denizen denizen;
	private Map<String, String> constants;
	private Map<String, String> assignmentConstants;
	private String assignment;

	public ConstantsTrait() {
		super("constants");
		denizen = (Denizen) Bukkit.getServer().getPluginManager().getPlugin("Denizen");
	}

	@Override 
	public void load(DataKey key) throws NPCLoadException {
		if (denizen == null) denizen = (Denizen) Bukkit.getServer().getPluginManager().getPlugin("Denizen");
		for (DataKey keyToLoad : key.getSubKeys()) {
			constants.put(keyToLoad.name().toUpperCase(), keyToLoad.getString(""));
		}
	}

	@Override public void save(DataKey key) {
		for (Entry<String, String> entry : constants.entrySet()) {
			key.setString(entry.getKey().toLowerCase(), entry.getValue());
		}
	}

	public void setConstant(String name, String value) {
		constants.put(name.toUpperCase(), value);
	}

	public void removeConstant(String name) {
		if (constants.containsKey(name.toUpperCase()))
			constants.remove(name.toUpperCase());
	}

	public boolean hasConstants() {
		return !constants.isEmpty();
	}

	public void describe(CommandSender sender, int page) throws CommandException {
		ScriptHelper sH = denizen.getScriptEngine().getScriptHelper();
		Paginator paginator = new Paginator().header("Constants");
		paginator.addLine("<e>NPC-specific constants: " + (hasConstants() ? "" : "None.") + "");
		paginator.addLine("<e>ID  <a>Name  <b>Value");
		int x = 0;
		for (Entry<String, String> constant : constants.entrySet()) {
			paginator.addLine("<e>" + x + "  <a> " + constant.getKey() + "  <b> " + constant.getValue());
			x++;
		}
		paginator.addLine("");

		if (npc.hasTrait(AssignmentTrait.class) && npc.getTrait(AssignmentTrait.class).hasAssignment()) {
			paginator.addLine("<e>Assignment-inherited constants:");
			paginator.addLine("<a>Name  <b>Value");
			for (Entry<String, String> constant : getAssignmentConstants().entrySet())
				paginator.addLine("<a>" + constant.getKey() + "  <b> " + constant.getValue());
			paginator.addLine("");

			if (!paginator.sendPage(sender, page))
				throw new CommandException(Messages.COMMAND_PAGE_MISSING);
			return;
		}

		if (!paginator.sendPage(sender, page))
			throw new CommandException(Messages.COMMAND_PAGE_MISSING);
	}
	
	public Map<String, String> getAssignmentConstants() {
		if (npc.hasTrait(AssignmentTrait.class) && npc.getTrait(AssignmentTrait.class).hasAssignment()) {
			// Has assignment
			if (assignment.equalsIgnoreCase(npc.getTrait(AssignmentTrait.class).getAssignment()))
				return assignmentConstants;
			
			
		}
		
		return assignmentConstants;
		
	}

}