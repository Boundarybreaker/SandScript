package space.bbkr.sandscript.helper;

import com.hrznstudio.sandbox.api.util.text.Text;

public class TextHelper {
	public static final TextHelper INSTANCE = new TextHelper();

	public Text literal(String text) {
		return Text.literal(text);
	}

	public Text translatable(String key) {
		return Text.translatable(key);
	}
}
