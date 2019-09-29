package space.bbkr.sandscript.helper;

import com.hrznstudio.sandbox.api.SandboxAPI;
import com.hrznstudio.sandbox.api.event.*;
import com.hrznstudio.sandbox.api.event.entity.LivingEvent;
import com.hrznstudio.sandbox.api.event.entity.PlayerEvent;
import com.hrznstudio.sandbox.api.util.Identity;
import space.bbkr.sandscript.ScriptManager;
import space.bbkr.sandscript.util.ScriptIdentity;

import javax.script.ScriptContext;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Helper to easily subscribe to events.
 */
public class EventHelper {
	private SandboxAPI api;
	private static Map<Identity, Class<? extends Event>> EVENTS = new HashMap<>();

	public EventHelper(SandboxAPI api) {
		this.api = api;
	}

	private Predicate getFilter(Object filter) {
		if (filter instanceof Predicate) {
			return (Predicate)filter;
		} else if (filter instanceof String) {
			ScriptIdentity runId = ScriptIdentity.of((String)filter);
			return event -> (boolean)ScriptManager.INSTANCE.runFunction(runId, (scriptId, ctx) -> {}, event);
		} else {
			throw new IllegalArgumentException("Must be passed a predicate or a script function ID!");
		}
	}

	private Consumer run(ScriptIdentity id) {
		return event -> {
			if (id.hasFunction()) {
				ScriptManager.INSTANCE.runFunction(id, (scriptId, ctx) -> {}, event);
			} else {
				ScriptManager.INSTANCE.runScript(id, ((scriptId, ctx) -> ctx.setAttribute("event", event, ScriptContext.ENGINE_SCOPE)));
			}
		};
	}

	/**
	 * Subscribe to an event, with the option for modifying the predicate to fire under.
	 * @param id The ID of the event to subscribe to.
	 * @param filter Either a new predicate object, or the ID of a script plus function to determine whether to run the event consumer.
	 * @param toRun The consumer to run when the event is fired.
	 */
	public void on(String id, Object filter, String toRun) {
		Identity eventId = ScriptIdentity.of(id);
		ScriptIdentity runId = ScriptIdentity.of(toRun);
		Predicate passedFilter = getFilter(filter);
		api.on(EVENTS.get(eventId), passedFilter, run(runId));
	}

	/**
	 * Subscribe to an event, with no configuration options.
	 * @param id The ID of the event to subscribe to.
	 * @param toRun The consumer to run when the event is fired.
	 */
	public void on(String id, String toRun) {
		Identity eventId = ScriptIdentity.of(id);
		ScriptIdentity runId = ScriptIdentity.of(toRun);
		api.on(EVENTS.get(eventId), run(runId));
	}

	/**
	 * Subscribe to an event, with the options for fire predicates and modifying priority.
	 * @param id The ID of the event to subscribe to.
	 * @param filter Either a new predicate object, or the ID of a script plus function to determine whether to run the event consumer.
	 * @param priority The priority to run at, made through {@link EventHelper#priority(String)}.
	 * @param toRun The consumer to run when the event is fired.
	 */
	public void on(String id, Object filter, Priority priority, String toRun) {
		Identity eventId = ScriptIdentity.of(id);
		ScriptIdentity runId = ScriptIdentity.of(toRun);
		Predicate passedFilter = getFilter(filter);
		api.on(EVENTS.get(eventId), passedFilter, priority, run(runId));
	}

	/**
	 * Subscribe to an event, with the option for modifying priority.
	 * @param id The ID of the event to subscribe to.
	 * @param priority The priority to run at, made through {@link EventHelper#priority(String)}.
	 * @param toRun The consumer to run when the event is fired.
	 */
	public void on(String id, Priority priority, String toRun) {
		Identity eventId = ScriptIdentity.of(id);
		ScriptIdentity runId = ScriptIdentity.of(toRun);
		api.on(EVENTS.get(eventId), priority, run(runId));
	}

	/**
	 * Subscribe to an event, with options for fire predicates and receiving cancelled events.
	 * @param id The ID of the event to subscribe to.
	 * @param filter Either a new predicate object, or the ID of a script plus function to determine whether to run the event consumer.
	 * @param receiveCancelled Whether cancelled events should still be passed.
	 * @param toRun The consumer to run when the event is fired.
	 */
	public void on(String id, Object filter, boolean receiveCancelled, String toRun) {
		Identity eventId = ScriptIdentity.of(id);
		ScriptIdentity runId = ScriptIdentity.of(toRun);
		Predicate passedFilter = getFilter(filter);
		api.on(EVENTS.get(eventId), passedFilter, receiveCancelled, run(runId));
	}

	/**
	 * Subscribe to an event, with the option to receive cancelled events.
	 * @param id The ID of the event to subscribe to.
	 * @param receiveCancelled Whether cancelled events should still be passed.
	 * @param toRun The consumer to run when the event is fired.
	 */
	public void on(String id, boolean receiveCancelled, String toRun) {
		Identity eventId = ScriptIdentity.of(id);
		ScriptIdentity runId = ScriptIdentity.of(toRun);
		api.on(EVENTS.get(eventId), receiveCancelled, run(runId));
	}

	/**
	 * Subscribe to an event, with options for priority and receiving cancelled events.
	 * @param id The ID of the event to subscribe to.
	 * @param priority The priority to run at, made through {@link EventHelper#priority(String)}.
	 * @param receiveCancelled Whether cancelled events should still be passed.
	 * @param toRun The consumer to run when the event is fired.
	 */
	public void on(String id, Priority priority, boolean receiveCancelled, String toRun) {
		Identity eventId = ScriptIdentity.of(id);
		ScriptIdentity runId = ScriptIdentity.of(toRun);
		api.on(EVENTS.get(eventId), priority, receiveCancelled, run(runId));
	}

	/**
	 * Subscribe to an event, with all configuration options.
	 * @param id The ID of the event to subscribe to.
	 * @param filter Either a new predicate object, or the ID of a script plus function to determine whether to run the event consumer.
	 * @param priority The priority to run at, made through {@link EventHelper#priority(String)}.
	 * @param receiveCancelled Whether cancelled events should still be passed.
	 * @param toRun The consumer to run when the event is fired.
	 */
	public void on(String id, Object filter, Priority priority, boolean receiveCancelled, String toRun) {
		Identity eventId = ScriptIdentity.of(id);
		ScriptIdentity runId = ScriptIdentity.of(toRun);
		Predicate passedFilter = getFilter(filter);
		api.on(EVENTS.get(eventId), passedFilter, priority, receiveCancelled, run(runId));
	}

	/**
	 * Get a priority to subscribe to events with.
	 * @param priority The priority to run at: "high", "normal", or "low". Not case sensitive.
	 * @return The priority object to pass in to a `on` method.
	 */
	public Priority priority(String priority) {
		switch(priority.toLowerCase()) {
			case "high":
				return Priority.HIGH;
			case "normal":
				return Priority.NORMAL;
			case "low":
				return Priority.LOW;
			default:
				throw new IllegalArgumentException("Priority must be high, normal, or low!");
		}
	}

	static {
		EVENTS.put(Identity.of("sandbox", "block/break"), BlockEvent.Break.class);
		EVENTS.put(Identity.of("sandbox", "item/get_arrow_type"), ItemEvent.GetArrowType.class);
		EVENTS.put(Identity.of("sandbox", "entity/living/death"), LivingEvent.Death.class);
		EVENTS.put(Identity.of("sandbox", "entity/player/death"), PlayerEvent.Death.class);
		EVENTS.put(Identity.of("sandbox", "screen/open"), ScreenEvent.Open.class);
		EVENTS.put(Identity.of("sandbox", "screen/close"), ScreenEvent.Close.class);
		EVENTS.put(Identity.of("sandbox", "enchantment/acceptable_item"), EnchantmentEvent.AcceptableItem.class);
		EVENTS.put(Identity.of("sandbox", "enchantment/compatible"), EnchantmentEvent.Compatible.class);
	}
}
