package me.aboullaite;

import java.util.ArrayList;
import java.util.List;

import co.aurasphere.botmill.core.annotation.BotConfiguration;
import co.aurasphere.botmill.fb.FbBotMillContext;
import co.aurasphere.botmill.fb.api.MessengerProfileApi;
import co.aurasphere.botmill.fb.model.api.messengerprofile.HomeUrl;
import co.aurasphere.botmill.fb.model.api.messengerprofile.persistentmenu.CallToActionNested;
import co.aurasphere.botmill.fb.model.api.messengerprofile.persistentmenu.PersistentMenu;
import co.aurasphere.botmill.fb.model.outcoming.factory.ButtonFactory;
import co.aurasphere.botmill.fb.model.outcoming.template.button.WebViewHeightRatioType;
import co.aurasphere.botmill.fb.model.outcoming.template.button.WebViewShareButton;

import co.aurasphere.botmill.core.internal.util.ConfigurationUtils;

public abstract class FbBotConfiguration {

	/** The Constant FB_BOTMILL_PAGE_TOKEN. */
	private static final String FB_BOTMILL_PAGE_TOKEN = "fb.page.token";

	/** The Constant FB_BOTMILL_VALIDATION_TOKEN. */
	private static final String FB_BOTMILL_VALIDATION_TOKEN = "fb.validation.token";

	public FbBotConfiguration() {
		this.buildFbBotConfig();
	}

	/**
	 * Builds the Fb bot config.
	 *
	 * @throws BotMillMissingConfigurationException
	 *             the bot mill missing configuration exception
	 */
	private void buildFbBotConfig() {

		FbBotMillContext.getInstance().setup(
				ConfigurationUtils.getEncryptedConfiguration().getProperty(FB_BOTMILL_PAGE_TOKEN),
				ConfigurationUtils.getEncryptedConfiguration().getProperty(FB_BOTMILL_VALIDATION_TOKEN));

	}
}