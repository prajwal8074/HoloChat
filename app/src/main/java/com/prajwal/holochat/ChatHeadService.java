package com.prajwal.holochat;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.provider.ContactsContract;
import android.view.*;
import android.widget.*;
import android.net.Uri;
import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Math;
import com.google.android.material.tabs.TabLayout;
import android.content.pm.ServiceInfo;
import android.text.Html;

//import com.google.android.gms.ads.*;
//import com.google.android.gms.ads.initialization.*;

public class ChatHeadService extends Service
{
    File dataDir;
    File botDataDir;
    File SpamDir;
    File CHDataDir;
    File NLDataDir;
    File ignoreTitlesFile;
    File ignoreTextsFile;
    File chatsPerAdFile;
	Bot bot;
	filter spamFilter;
	Icon botIcon;
	Random random;
	String appPkg;
	Notification.Builder notification;
	int notificationId;
	int foregroundType;
	File sentsFile;
	File ignoredsFile;
	File autoSendTosFile;
	File autoSendTosUFile;
	File autoSendMsgsFile;
	File autoSendMsgsUFile;
	File chatHeadSizeDivFile;
	File chatHeadSensitivityFile;
	File hapticsEnabledFile;

    WindowManager mWindowManager;
    RelativeLayout mChatHeadView;
	RelativeLayout mRemoveView;
	RelativeLayout mBlackBackView;
	RelativeLayout chatListView;
	ImageView chatHead;
	TextView chatCircleMessages;
	TextView chatCircleReplies;
	ImageView iconProjection;
	ListView iconList;
	LinearLayout chatProjectionDown;
	LinearLayout chatProjectionUp;
	ListView chatList;
	WindowManager.LayoutParams mBlackBackViewParams;
	WindowManager.LayoutParams mExpandedViewParams;
	WindowManager.LayoutParams mRemoveViewParams;
	WindowManager.LayoutParams mChatHeadViewParams;
	WindowManager.LayoutParams iconProjectionParams;
	WindowManager.LayoutParams iconListParams;
	WindowManager.LayoutParams chatProjectionDownParams;
	WindowManager.LayoutParams chatProjectionUpParams;

	int screenWidth;
	int screenHeight;
	float xdpi;
	float ydpi;
	float chatHeadSizeDiv;
	int chatHeadSensitivity;
	float screenDensity;
	int chatHeadWidth;
	int chatHeadHeight;
	int chatListWidth;
	int chatListHeight;
	int MATCH_PARENT;
	int WRAP_CONTENT;
	boolean isTouched = false;
	boolean justTouched = false;
	boolean longTouched = false;
	boolean isExpanded = false;
	boolean moving = false;
	boolean removing = false;
	boolean isToLeft = false;
	boolean isToTop = true;
	boolean directReply = false;

	//standard measures
	final long timeTotal = 500;
	final long delta = 5;
	final int stepsTotal = (int) (timeTotal / delta);

	CountDownTimer sleepTimer;
	CountDownTimer sleepShrinkTimer;
	Thread loadThread;
	android.content.ClipboardManager clipboardC;

	String[] sentNames;
	String[] ignoredNames;
	ArrayList<String> autoSendTos;
	ArrayList<String> autoSendTosU;
	ArrayList<String> autoSendMsgs;
	ArrayList<String> autoSendMsgsU;

	//chats related
	int SENDER = 0;
	int MESSAGE = 1;
	ArrayList<String> senders;
	ArrayList<String> messages;
	ArrayList<String> pkgs;
	ArrayList<String> sendTos;
	ArrayList<Icon> icons;
	ArrayList<Icon> buttonIcons;
	ArrayList<String> buttonTexts;
	ArrayList<String> ids;
	ArrayList<String> replies;
	ArrayList senderColors;
	ArrayList<Bitmap> imgs;
	ArrayList<Boolean> autoSends;
	ArrayList<Boolean> sendButtonClicks;
	ArrayList<Boolean> chatRemoves;
	ArrayList<Boolean> chatSpams;
	ArrayList<Boolean> chatNotSpams;
	ArrayList<Boolean> seens;
	ArrayList<Parcelable> chatListStates;
	int chatRemovesCount = 0;
	Parcelable chatListStateEmpty;
	ArrayList<TextView> replyViews;
	ArrayList<Boolean> replyViewSelecteds;
	ArrayList<ImageView> sendUps;
	ArrayList<ImageView> sendDowns;
	ArrayList<ImageView> refreshViews;
	String groupConStr = " @ ";
	boolean showingChat = false;
	boolean updated = false;
	boolean created = false;
	boolean visible = false;
	boolean initialised = false;
	boolean voluntaryDestroing = false;
	boolean isShrinked = false;
	//projection related
	float tmpScaleX;
	float tmpScaleY;
	//listView Related
	int firstVisibleIcon;
	int totalVisibleIcons;
	int initSize;
	int finalSize;
	int extraSize;
	int firstVisibleChat;
	int totalVisibleChats;
	int notChatsTop;
	int notChatsBottom;
	int chatViewsCount;
	int chatLoading = 0;
	int chatLoadingViewIndex = 0;
	boolean quickSend = false;
	int notSents = 0;
	//for each chat
	ArrayList<Boolean> sent;
	//local vars
	boolean reverse = false;
	String loadedReply = "";

	int CHAT_ALL = -1;
	String PENDING = "(pending)";
	String LOADING = "(loading)";
	String MESSAGE_EMPTY = "‎";
	int chatInFocus = CHAT_ALL;
	boolean loaded = true;
	int chatsPerAd;

	int REQ = 0;
	int RES = 1;
	ArrayList<String[]> toLearn;
	
	String[][]emojis = new String[][]{
		new String[]{"╭∩╮", "凸  ಥ ڡ ಥ 凸  ( ◕︿◕ )╭∩╮  凸(⊙益⊙✖)  凸(0﹏0)凸  (͡° ͜ʖ ͡°)凸  凸 ಠ▂ಠ) 凸  t(-.-t)  (｀△´＋凸）  (￣∀￣)╭∩╮  (* ͜ʖ*)凸  (◍▼∀▼◍)╭∩╮  凸(^╹ ｪ ╹ﾒ^)  ୧༼ಠ◇ಠ╭∩╮ ༽  (＾益＾）ノ  （◆_◆メ)凸  ╭∩╮(＃￣0￣)╭∩╮  (-_-) 凸  凸〳 (>皿<) 〵凸  (⩺!⩹)凸  ( ` ﾛ ´ )╭I╮  ╭∩╮(˘◡˘✿)  ╭∩╮ʕ◕ᴥ◕ ʔ  ᶘ (ᵒᴥᵒ ᶅ)┌∩┐  凸ʕ •̀ᴥ•́ʔ凸  ╭∩╮（°︿︶°）╭∩╮  (︶ ͜ʖ͡︶)╭∩╮  ╭∩╮(￣_￣)╭∩╮  凸(￣0￣)凸  ┌∩┐(︶◡︶)┌∩┐  (☉⍊☉)╭∩╮  (⩹ ͜ʖ ͡⩺)╭∩╮  (U☉Ꮂ☉)凸  ╭∩╮(•_• )╭∩╮  凸( ` ﾛ ´ )凸  (◣◢)凸  (⩾U⩽)╭∩╮  (；ΟДΟ)凸  凸(￣ヘ￣)  凸(￣□￣」)  ╭∩╮(¬Ꮂ¬)  ╭∩╮(`  ഌ`  )  (Uꖘᴥꖘ)╭∩╮"},
		new String[]{"▨-▨¬", "ㄏ(▼∀▼)ㄏ  v(▼益▼ﾒ)ゞ  (つ▼¯▼)つ  ┌(▼¯▼)┘  ( ͡⎚ . ͡⎚)  (ﾉ◉皿◉)ﾉ  ﴾ﾉ■ω■﴿ﾉ  ☀(̿▼-▼¬)̄  (⌬̀_⌬́)  (✦つ▀・▀)つ  (७ ▼ل͟▼)७  (▨ ▨)  ┌(▀Ĺ̯▀)┐  ( ͡⎚ U ͡⎚)  ┌(⌐▼¯▼)┘  (⌐▼_ ▼)  щ( ▼ﾛ▼щ)  (▀̿̿Ĺ̯̿̿▀̿ ̿)ლ  (ɔˆ▨_▨)  ▨-▨¬  ༼ ⌐▼ل͜▼༽  ᕕ(ง⌐□ل͜□)ᕗ  (⌐▼_▼)  (◥▶.◀◤)  ¯\\_(ᴼ_ᴼ)_/¯  ☞(⌐■.■)☞  (つ▀¯▀)つ  └( ▼o▼ )┐  (ﾒ▰ヮ▰)y-～  ⊂(▼¯▼⊂)  (ﾒ ▰_▰)  ʕ■Ѡ■ʔ  ╰▄◠▄╯  ლ(▔▀ ‿ ▀ )❤  ( ｷ⌐■⊿■)ﾉ  (■_■¬)  ヽ(❤▾❤)ﾉ  ʕ▼!▼ʔ  (▼_▼#)  ┌(メ▨▾▨)┘  (■皿■ ﾒ)ﾉ  (ﾒ■_■)  ( ▀ ω ▀ )  ( ▼Д▼)y─┛  ╰(▀̿̿Ĺ̯̿̿▀̿ ̿)╯  (⌐■ل͟■)  (*▼Д▼)/ノ  ┬━┬ノ(■Д■ノ)  ☜(▼ل͟▼)☞  ᕮ■_■ᕭ  ( *▼o▼*)  (*▼o▼*)ノ  ◯ل͜◯¬  (⌐■-■)  ( ͡⎚‿ ͡⎚)ㄏ  (▼皿▼)  ( ╬◣ 益◢)o  (̿▼-▼¬˵)  ༼⌐■ل͟■༽  (o◥▶ᴥ◀◤o)  ง( ͡° ͜ʖ ͡°)>⌐ ▀̿ Ĺ̯ ▀̿  └( ▼▼ )┐  (̿▀̿ ̿Ĺ̯̿̿▀̿ ̿)"},
		new String[]{"▬ι══>", "▬ι══════>  ¤=[]:::::>  o═[::::::::::::::::::::::::  o()xxx[{::::::::::::::::::::::::>  ◒((|||||))==========>  ▬ι════════ﺤ  @═[]::::::::::::::::>  @==[{::::::::::::::::::::::::::::::>  (｀▽´)=o==[]::::::::::>  ( ͝° ͜ʖ͡°)▬▬ι═══════ﺤ  (^✺ ﻌ ✺^)ง▄ ┻┳>  ( ╹U╹)ง-]——-  (✿˵≧ω≦)▬▬ι═══════ﺤ  (*ﾟ∀ﾟ)つ⊃-(===>  ξ(✿ ❛‿❛)▬ι═════>  (∩ ͡ ° ʖ ͡ °) ⊃＝lニニフ  ੧| ˃́ ▾ ˂̀ |⊃¤=(————-  (◍⊗‿⊗)▬▬ι═══════ﺤ  (ง ͠ ᵒ̌ Дᵒ̌ )⊃-(==>  ᗜ ಠ o ಠ)¤=[]:::::>  Ψ(Φ皿Φ)))ง-]:::::>  ( ง0Д0)っ▬▬ι═══════ﺤ  ヽ༼ಥل͟ಥ༽₪₪₪₪§|(ΞΞΞΞΞΞΞΞΞΞΞΞ>   ( ￣□￣)_¤=[]:::::>  -═══════ι▬▬ﺤ    (✿ᴖoᴖ))ง-];;;;;;;;;>  xxxx [ι═══════ﺤ  (]xxx[}(ΞΞΞ=======>   ╰༼.◕ヮ ◕.༽つ¤=[]:::::::::>  (⁰Д⁰)⊃-▬ι═══════ﺤ  (ง ͠ ᵒ̌ Дᵒ̌)ง-]—-  ( ͡° ͜ʖ ͡° )¤=[]:::::>  (]===[)::::::::::::::::::::::>   (ง ͠° ͟ل͜ ͡°)¤=[]:::::>  ☾▬▬{::::::::::::::::::>  (ﾒ▼_▼)ﾉ)xx[;;;;;;;;;>  ( ͡☆ ͜ʖ ͡☆ )o()xxxx[{::::::>  ⊃o()-[{:::::::::::::::::::::::::::::>  ↑o ()≡xxxxx [{::::::::::::::::::>↑  cxxx{}:;:;:;:;:;:;:;:;:;:;:;:;:;:;:;:;:;:;>   )xx[;;;;;;>  ╰(༼⇀︿⇀༽つ-]═───  ¯\\_( ͡° ͜ʖ ͡° )⊃-(===>  ( ∩ ͡ ° ʖ ͡ ° ) ง-]—-  (ง ͠ ᵒ̌ Дᵒ̌ )つ¤=[]———  -═══ι▬∩༼˵☯‿☯˵༽つ¤=[]:::::>  ( `ω´ )۶▬ι═══════ﺤ  (❁ꈍωꈍ)o===:::::::::>  <-════ι▬(•̀_•́ผ)  o()========>"},
		new String[]{"⌐╦╦═─", "╾━╤デ╦︻(˙ ͜ʟ˙ )  ( -_●)╦╤─▸  <::::[]=¤ (▀̿̿Ĺ̯̿̿▀̿ ̿)  »-(¯`·.·´¯)->  (✿❛.❛)▄︻┻┳  (♡≧ω≦)︻┻┳══━一一  (˃ᴗ˂)⌐╦╦═─  ┌( ͝° ͜ʖ͡°)=ε/̵͇̿̿/’̿’̿ ̿  ︻デ═一  ( ❛‿❛)o┳-  ▄︻̷̿┻̿═━一  ( ͡° ͜ʖ ͡°)=ε/̵͇̿̿/'̿  ̿̿¯̿̿¯̿̿'̿̿)͇̿̿)̿̿ '̿̿\\̵͇̿̿\\=(•̪●)  ⤜(ⱺ ʖ̯ⱺ)⤏  ╾━╤デ╦︻(▀̿Ĺ̯▀̿ ̿)  (◕_◕)=ε/̵͇̿̿/''''̿''''̿ ̿  ( ´-ω･)▄︻┻┳═一  (ง ͠ ᵒ̌ Дᵒ̌ )¤=[]:::::>  (⌐■_■)--︻╦╤─ - - -  ━╤デ╦︻(▀̿̿Ĺ̯̿̿▀̿ ̿)  ͇ ͇ ͇ ͇ ͇ ̳ ̳ ̜˛ۣۣ̜ﮧ̜ۣﮧ̜ۣ‚̥◞(ە̀_̖́٥ )  ¯¯̿̿¯̿̿'̿̿̿̿̿̿̿'̿̿'̿̿̿̿̿'̿̿̿)͇̿̿)̿̿̿̿ '̿̿̿̿̿̿\\̵͇̿̿\\=(•̪̀●́)=o/̵͇̿̿/'̿̿ ̿ ̿̿  (❦◡❦)︻デ═一  (•̪●)=/̵/’̿̿ ̿ ̿ ̿ ̿  !!( ’ ‘)ﾉﾉ⌒●~*  ε/̵͇̿̿/’̿’̿ ̿(◡︵◡)  ︻╦̵̵͇̿̿̿̿══╤─  (✿ ❛ω❛)▄ ┻┳★  ლ(~•̀︿•́~)つ︻̷┻̿═━一  (✿ ❛_❛)╦╦═  ︻╦̵̵͇̿̿̿̿══╤─  ᕦ(▀̿ ̿ -▀̿ ̿ )つ︻̷┻̿═━一-  ╾━╤デ╦︻( ▀̿ Ĺ̯ ▀̿├┬┴  (✿❛U❛)▄-┻┳-  ( う-´)づ︻╦̵̵̿╤── \\(˚☐˚”)/  ( う-´)づ︻╦̵̵̿╤──  ( ͡° ͜ʖ ͡°)=ε/̵͇̿̿/'̿̿ ̿ ̿̿ ̿ ̿  ( ͡° ͜ʖ ͡°)︻̷┻̿═━一-  ╾━╤デ╦︻ԅ། ･ิ _ʖ ･ิ །ง  ξ(✿ ❛‿❛)ξ︻┻┳══━一  ’̿’\\̵͇̿̿\\=(•̪●)=/̵͇̿̿/’̿̿ ̿ ̿ ̿  (ノ￣^￣)ノ!≡≡≡≡=━┳━☆ (⌯˃̶᷄ ﹏ ˂̶᷄⌯)  ( -_･) ︻デ═一 ▸  >̿ ̿̿ ̿̿ ̿'̿'\\̵͇̿̿\\з= ( ▀ ͜͞ʖ▀) =ε/̵͇̿̿/’̿’̿ ̿ ̿̿ ̿̿ ̿̿  ( ´-ω･)⌐╦╦═─"},
		new String[]{"╰⋃╯", "(• )( •)  ╰⋃╯  (‿ˠ‿)  (｡ ! ｡)  (✿ಸ‿ಸ )  ლ(◉‿- ლ)  ( • )( • ) ԅ(∂‿∂ԅ)  (＾_\")-cԅ(‾⌣‾ԅ)  ( ⚈ᴗ˃)っ✂╰⋃╯  (ᇴ‿ฺᇴ)ノ  ^.~)σ  (♡’◡˘)✿  (´• _´• )σ  (‿!‿) (´◑ ʖ◐｀)(‿ˠ‿)  8===D  ᕕ( ゜ᐛ゜ )ᕗ  (✿۵´･ᴗ･◦)♡  ༼ つΘ ‿Θ ༽つ╰⋃╯  (‿!‿) ԅ(Ő‿Őԅ)  (✿*⸰◡⸰)づ♡  ( • )( • ) ԅ(´ཀ` ⑊)  (‿!‿) ԅ(°ε°ԅ)  (〴¬ᴗ¬)〴  (˘_˘\")-cԅ(‾ε‾ԅ♥)  (•.•)❤  ⋐(♥^ε^♥ ⋐)  (^‿^) ╰⋃╯  (’‿’)っ✂╰⋃╯  (͡＠.＠) ***  (つ˘ᐛ ˘)つ╰⋃╯  ᕕ( ˘⌣˘)ᕗ ᕕ❤ᕗ  (‿ˠ‿)୨(◉ʖ◉)୧(‿ˠ‿)  ˁ ˝•ᴗ•ˀσ👌"},
		new String[]{"ʕ•ᴥ•ʔ", "【≽ܫ≼】  (ﾐↀ ᆽↀﾐ)  ʕ•ᴥ•ʔ  ＾ዋ⋏ዋ＾  ≧☉ᆺ☉≦  (ﾐゝᆺ⁰ﾐ)  ฅ(ﾐ。ꀾ 。ﾐ)∫  (^-.-^J  └(=•^‥^=)┐  ^ↀᴥↀ^  (⁎˃ᆺ˂)  (ﾐᗜᆽᗜﾐ)∫  ฅ(ﾐꏿᆽꏿ｀ﾐ)∫  (ﾉ≧ڡ≦)  ( =꒡ω꒡= )  <⦿⽘⦿>  ฅ^•ﻌ•^ฅ  (ﾐゝᆽゝﾐ)  ∩(ዕωዕ)∩  (ﾐ꒡ᆽ꒡ﾐ)  (ﾐ≅ ﻌ ≅ﾐ)∫  ฅ(˃ᆺ˂)∫  ミʘ̥ｖʘ̥彡  \\(≧∇≦)/  ~=[,,_,,]:3  (ﾐゝᆽ☆ﾟﾐ)  (ﾐꏿᆽ◕ﾐ)  ( ^ ①ω① ^)  (=^･ｪ･^=))ﾉ彡☆  (=^･ｪ･^=)  ฅ(ﾐzᆽzﾐ)∫  (=•́ᆽ•̀=)  (ٛ •_•´ ٛ )  (ﾐ・ᆽ・ﾐ)  (=๏ᆽ๏=)  ₍ᐢ•ﻌ•ᐢ₎  (๑*ᆽ*๑)  ~(^._.)  (^._.^)ﾉ  (^・ω・^ )  ／(^ x ^)＼  (=^･ω･^=)"},
		new String[]{"(✖︹✖)", "(х-х)  ʕx‸xʔ  ƈ ͡ (ŏ̥̥̥̥םŏ̥̥̥̥) ͡  ╭( _ )╮  و ̑̑( =ↀωↀ=)  Ｕ×╭╮ ×Ｕ  (๏_๏;）  ( ͡x ͜ ͡x ) ╦̵̵̿╤──≈  ᕮ⨱ヘ⨱ᕭ  ⤜(>_>)⤏  ╭( >_< )╮  ᑫ⨴д⨵ᑷ  ฅ(=༝㉨༝=)∫  ᕕ( ͡° ͜ʖ ͡° )¬  [⨶.⨶]  \\(⨶ㅿ⨶)/  ✺◟(o㉨o)◞✺  〈XωX〉  ﴾⨶ ʖ̯⨶﴿  (✿•◡•)⌐╦̵̵̿╤─  (´｀‸｀`)  (´×︹×`)  (⨱Д⨱)  ⤜(✘෴✘)⤏  ヽ༼x﹏x༽ﾉ  (=ｘ｡ｘ=)  (=ⓧܫⓧ=)∫  ( =ｘ_ ｘ=)  ฅ(=X㉨X=)ฅ  ◝(๑⁺∀⁺ ๑)◞՞  ^_^  (✘Ĺ̯✘)  (๑☉﹏☉๑)❤  (✘-✘)  （ >ഌ <）  ＝( ￣o￣ ;)⇒  [̲̅$̲̅(̲̅⨴o⨵)̲̅$̲̅]  ( ཀ _ ཀ)  ﴾⨱﹏⨱﴿  {@˟÷˟@}  (x㉨x)  （×ω×)人  (╹ｘ╹;)  ʕx‸xʔ  【xдx】  ✺◟(⨱__⨱)◞✺  ✿(x ͜つx)✿  ヽ(×∀×ゞ✿)  (, ,-`_-)  ◉✞◉  (✖︹✖)  ╭(×_×)╮  {@˟ꈊ˟@}  ᕳ⨱ ͟ʖ⨱ᕲ  (❀✖▾✖)  ✿(TT o TT%)  ✞óx⸑ó✞  [¬ºд °] ¬  ( ❛ 0 ❛)▄-┻┳  (((=ェ=)))"}
	};

	boolean hapticsEnabled = false;

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO: Implement this method
		return onBind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, final int startId)
	{
		// TODO: Implement this method
		if(intent != null)
		{
			String type = intent.getStringExtra("type");

			if(type.equals("init"))
			{
				if(mWindowManager == null)
				{
					//creating a notification to run the service in foreground
					notificationId = 42069;
					Intent notificationIntent = new Intent(this, AppActivity.class);
					notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					notification = new Notification.Builder(this)
						.setContentTitle("0 messages")
						.setContentText("0 messages")
						.setSubText("0 spams")
						.setTicker("Chat Head Active")
						.setSmallIcon(R.drawable.ic_logo_transparent)
						//.setLargeIcon(Icon.createWithResource(this, R.drawable.ic_logo))
						.setColor(Color.argb(Integer.MAX_VALUE, 0, 255, 251))
						.setContentIntent(PendingIntent.getActivity(this, 0, 
							notificationIntent, PendingIntent.FLAG_MUTABLE));
					
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
					{
						String CHANNEL_ID = getPackageName().replace(".", "_");// The id of the channel. 
						notification.setChannelId(CHANNEL_ID);
						NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "Chat Head Active Notification", NotificationManager.IMPORTANCE_LOW);	
						NotificationManager mNotificationManager =
							(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						mNotificationManager.createNotificationChannel(mChannel);
					}
			        foregroundType = 0;
			        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			            foregroundType = ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;
			        }
					startForeground(notificationId, notification.build(), foregroundType);

					/*MobileAds.initialize(ChatHeadService.this, new OnInitializationCompleteListener(){
            
						@Override
						public void onInitializationComplete(InitializationStatus initializationStatus){}
					});*/

					random = new Random();
					dataDir = getFilesDir();
					botDataDir = new File(dataDir, "BotData");
					SpamDir = new File(getExternalFilesDir(null), "Spam");
					CHDataDir = new File(dataDir, "ChatHeadData");
					NLDataDir = new File(dataDir, "NotificationListenerData");
					ignoreTitlesFile = new File(NLDataDir, "ignoreTitles");
					ignoreTextsFile = new File(NLDataDir, "ignoreTexts");
					chatsPerAdFile = new File(CHDataDir, "chatsPerAd");
					bot = new Bot(botDataDir);
					spamFilter = new filter(SpamDir.getAbsolutePath());
					try{spamFilter.init();}catch(FileNotFoundException e){}
					botIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_logo_small);
					appPkg = getPackageName();

					//init vars
					isTouched = false;
					justTouched = false;
					longTouched = false;
					isExpanded = false;
					moving = false;
					removing = false;
					isToLeft = false;
					isToTop = true;
					isShrinked = false;

					chatRemovesCount = 0;

					final Handler handler = new Handler();

					final CountDownTimer refreshLoadingTimer = new CountDownTimer(360, 1){

						@Override
						public void onTick(long p1)
						{
							// TODO: Implement this method
							if(showingChat)
								if(chatInFocus == CHAT_ALL || chatInFocus == chatLoading)
								{
									if(refreshViews != null && chatLoadingViewIndex > -1 && chatLoadingViewIndex < refreshViews.size() && refreshViews.get(chatLoadingViewIndex) != null)
									{
										refreshViews.get(chatLoadingViewIndex).setRotation(360-p1);
									}
								}
						}

						@Override
						public void onFinish()
						{
							// TODO: Implement this method
							if(showingChat)
							{
								if(chatInFocus == CHAT_ALL || chatInFocus == chatLoading)
								{
									if(refreshViews != null && chatLoadingViewIndex > -1 && chatLoadingViewIndex < refreshViews.size() && refreshViews.get(chatLoadingViewIndex) != null)
									{
										refreshViews.get(chatLoadingViewIndex).setRotation(0);
										
										String replyStr = replyViews.get(chatLoadingViewIndex).getText().toString();

										if(replyStr.equals(LOADING))
											start();
										else
											if(replyStr.trim().equals("") || replyStr.equals(PENDING))
											{
												refreshViews.get(chatLoadingViewIndex).setVisibility(View.GONE);
											}
									}
								}
							}
						}
					};

					loadThread = new Thread()
					{
						String toastTxt = "";

						@Override
						public void run()
						{
							while(!isInterrupted())
							{
								if(!loaded)
								{
									if(toLearn != null)
									{
										while(toLearn.size() > 0)
										{
											if(!isInterrupted())
											{
												bot.learn(toLearn.get(0)[REQ], toLearn.get(0)[RES]);
												toLearn.remove(0);
											}
										}
									}

									for(int i = 0;i < messages.size();i++)
									{
										if(!isInterrupted())
										{
											final int chatIndex = i;
											if(!chatRemoves.get(chatIndex))
											{
												final int chatViewIndex = chatInFocus == CHAT_ALL? getChatViewIndex(chatIndex) : 0;

												if(replies.get(chatIndex).equals((PENDING)))
												{
													try{
														if(!messages.get(chatIndex).trim().equals(""))
														{
															File fr = new File(SpamDir, "tmp");
															writeToFile(fr, new String[]{messages.get(chatIndex)});
															FileReader reader = new FileReader(fr);
															double result = spamFilter.classification(reader);

													        if(result==1)
													        	if(!isInterrupted())
													        	{
													        		chatNotSpams.set(chatIndex, false);
													        		chatRemoves.set(chatIndex, true);
													        		chatRemovesCount++;
																	handler.post(new Runnable(){

																		@Override
																		public void run()
																		{
																			onChatDataChange();
																			refreshNotification();
																		}
																	});
													        	}
													        fr.delete();
														}
												    }catch (IOException e){
												    }finally
												    {
														if(!chatRemoves.get(chatIndex))
														{
															replies.set(chatIndex, LOADING);
															chatLoading = chatIndex;
															chatLoadingViewIndex = chatViewIndex;
															
															if(!isInterrupted())
															{
																if(visible)
																	if(showingChat)//(chatList.getAdapter() != null)
																	{
																		if(chatInFocus == CHAT_ALL || chatInFocus == chatIndex)
																		{
																			if(replyViews != null && chatViewIndex < replyViews.size() && replyViews.get(chatViewIndex) != null && replyViews.get(chatViewIndex).getText().toString().equals(PENDING))
																			{
																				if(refreshViews != null && chatViewIndex < refreshViews.size() && refreshViews.get(chatViewIndex) != null)
																				{
																					handler.post(new Runnable(){

																							@Override
																							public void run()
																							{
																								// TODO: Implement this method
																								replyViews.get(chatViewIndex).setText(LOADING);
																								refreshLoadingTimer.cancel();
																								refreshViews.get(chatViewIndex).setRotation(0);
																								refreshViews.get(chatViewIndex).setVisibility(View.VISIBLE);
																								refreshLoadingTimer.start();
																							}
																						});
																				}
																			}
																		}
																	}
															}

															/*try{
											                    Socket s=new Socket(InetAddress.getByName("103.107.115.85"),9000);
											                    DataInputStream din=new DataInputStream(s.getInputStream());
											                    String portStr = din.readUTF();
											                    din.close();
											                    s.close();
											                    
											                    Socket s1=new Socket(InetAddress.getByName("103.107.115.85"),Integer.parseInt(portStr));  
											                    DataOutputStream d1out=new DataOutputStream(s1.getOutputStream());  
											                    DataInputStream d1in=new DataInputStream(s1.getInputStream());
											                    d1out.writeUTF("getReply:"+messages.get(chatIndex));  
											                    d1out.flush();  
											                    loadedReply = d1in.readUTF();
											                    d1in.close();
											                    d1out.close();
											                    s1.close();
											                }catch(Exception e){
											                	loadedReply = "";
											                }finally
											                {*/
											                	//if(loadedReply.trim().equals(""))
																	loadedReply = bot.getReply(messages.get(chatIndex))[0];

																replies.set(chatIndex, loadedReply);
																
																if(!chatRemoves.get(chatIndex))
																{
																	sendButtonClicks.set(chatIndex, false);

																	if(!isInterrupted())
																	{
																		if(visible)
																			if(showingChat)//chatList.getAdapter() != null)
																			{
																				if(chatInFocus == CHAT_ALL || chatInFocus == chatIndex)
																				{
																					if(replyViews != null && chatViewIndex < replyViews.size() && replyViews.get(chatViewIndex) != null && replyViews.get(chatViewIndex).getText().toString().equals(LOADING))
																					{
																						if(refreshViews != null && chatViewIndex < refreshViews.size() && refreshViews.get(chatViewIndex) != null)
																						{
																							handler.post(new Runnable(){

																									@Override
																									public void run()
																									{
																										// TODO: Implement this method
																										replyViews.get(chatViewIndex).setText(loadedReply);

																										refreshLoadingTimer.cancel();
																										refreshViews.get(chatViewIndex).setRotation(0);

																										if(loadedReply.trim().equals(""))
																											refreshViews.get(chatViewIndex).setVisibility(View.GONE);
																									}
																								});
																						}
																					}
																				}
																			}

																		/*if(autoSends.get(chatIndex))
																		{
																			if(((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
																			   && !ids.get(chatIndex).equals(""))
																			   && requestReplyIntents(chatIndex, senders.get(chatIndex), messages.get(chatIndex), replies.get(chatIndex), ids.get(chatIndex)))
																				toastTxt = "autosent to " + senders.get(chatIndex);
																			else
																				toastTxt = "failed to autosend " + senders.get(chatIndex);

																			if(!isInterrupted())
																			{
																				handler.post(new Runnable(){

																						@Override
																						public void run()
																						{
																							// TODO: Implement this method
																							Toast.makeText(getApplicationContext(), toastTxt, Toast.LENGTH_SHORT).show();
																							refreshNotification();
																						}
																					});
																			}
																		}*/
																	}
																	chatLoading = -1;
																	chatLoadingViewIndex = -1;
																}
														}else{
															handler.post(new Runnable(){
																@Override
																public void run()
																{
																	// TODO: Implement this method
																	replies.set(chatIndex, "");
																	replyViews.get(chatViewIndex).setText("");
																}
															});
														}
													}
												}
											}
										}
									}
									chatLoading = -1;
									chatLoadingViewIndex = -1;
									loaded = true;
									handler.post(new Runnable(){

											@Override
											public void run()
											{
												// TODO: Implement this method
												onThreadLoadFinish();
											}
										});
								}
							}
						}
					};
					loadThread.setDaemon(true);
					loadThread.setPriority(Thread.NORM_PRIORITY);

					clipboardC = (android.content.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

					autoSendTosFile = new File(CHDataDir, "autoSendTos");
					autoSendTosUFile = new File(CHDataDir, "autoSendTosU");
					autoSendMsgsFile = new File(CHDataDir, "autoSendMsgs");
					autoSendMsgsUFile = new File(CHDataDir, "autoSendMsgsU");
					sentsFile = new File(CHDataDir, "sents");
					ignoredsFile = new File(CHDataDir, "ignoreds");
					chatHeadSizeDivFile = new File(CHDataDir, "chatHeadSizeDiv");
					chatHeadSensitivityFile = new File(CHDataDir, "chatHeadSensitivity");
					hapticsEnabledFile = new File(CHDataDir, "hapticsEnabled");

					updated = false;
					created = true;
					visible = false;

					MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
					WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

					screenWidth = getResources().getDisplayMetrics().widthPixels;
					screenHeight = getResources().getDisplayMetrics().heightPixels;
					screenDensity = getResources().getDisplayMetrics().density;

					xdpi = getResources().getDisplayMetrics().xdpi;
					ydpi = getResources().getDisplayMetrics().ydpi;

					try
					{
						chatHeadSizeDiv = Float.valueOf(readFromFile(chatHeadSizeDivFile, "SEPARATOR_NEW_LINE")[0]);
						chatHeadSensitivity = Integer.parseInt(readFromFile(chatHeadSensitivityFile, "SEPARATOR_NEW_LINE")[0]);
						hapticsEnabled = Boolean.parseBoolean(readFromFile(hapticsEnabledFile, "SEPARATOR_NEW_LINE")[0]);
						
						chatsPerAd = 3;//change this
						String[] chatsPerAdLines = readFromFile(chatsPerAdFile, "SEPARATOR_NEW_LINE");
						if(chatsPerAdLines.length > 0)
							chatsPerAd = Integer.parseInt(chatsPerAdLines[0]);
						else
							chatsPerAdFile.delete();chatsPerAdFile.createNewFile();writeToFile(chatsPerAdFile, new String[]{String.valueOf(chatsPerAd)});
					}catch (IOException e)
					{}catch (NumberFormatException e)
					{}
					chatHeadWidth = (int)(xdpi / chatHeadSizeDiv);
					chatHeadHeight = chatHeadWidth;

					chatListWidth = screenWidth - chatHeadWidth * 2;
					chatListHeight = screenHeight - chatHeadHeight * 2;

					mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);

					voluntaryDestroing = false;

					sleepShrinkTimer = new CountDownTimer(timeTotal/4, delta){

						@Override
						public void onTick(long p1)
						{
							// TODO: Implement this method
							chatHead.setAlpha(0.75f+0.25f*(p1/(float)(timeTotal/4)));
							chatHead.setScaleY(1f-(0.25f*(1f-(p1/(float)(timeTotal/4)))));

							if(isToLeft)
							{
								chatHead.setX((int)((-chatHeadHeight/8)*(1f-(p1/(float)timeTotal/4))));
								chatHead.setScaleX(1f-(0.25f*(1f-(p1/(float)(timeTotal/4)))));
							}
							else
							{
								chatHead.setX((int)((chatHeadHeight/8)*(1f-(p1/(float)timeTotal/4))));
								chatHead.setScaleX(-1f+(0.25f*(1f-(p1/(float)(timeTotal/4)))));
							}

							mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);
						}

						@Override
						public void onFinish()
						{
							// TODO: Implement this method
							chatHead.setAlpha(0.5f);
							chatHead.setScaleY(0.75f);

							if(isToLeft)
							{
								chatHead.setX(-chatHeadWidth/8);
								chatHead.setScaleX(0.75f);
							}
							else
							{
								chatHead.setX(chatHeadWidth/8);
								chatHead.setScaleX(-0.75f);
							}
							
							isShrinked = true;
						}

					};

					sleepTimer = new CountDownTimer(4500, 4500){

						@Override
						public void onTick(long p1)
						{
							// TODO: Implement this method
						}

						@Override
						public void onFinish()
						{
							// TODO: Implement this method
							if(!isExpanded && !isTouched)
							{
								if(visible)
								{
									if(chatHead.getAlpha() == 1f)
									{
										sleepShrinkTimer.start();
										chatCircleMessages.setVisibility(View.INVISIBLE);
										chatCircleReplies.setVisibility(View.INVISIBLE);
									}
								}
							}
						}
					};

					////////////////////////////creating just layouts not interactable stuff///////////////////////////////

					//create a black back view
					mBlackBackView = new RelativeLayout(this);
					mBlackBackView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
					mBlackBackView.setBackgroundColor(Color.BLACK);
					mBlackBackView.setAlpha(0.5f);

					//set params
					int typeOverlay = (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
					mBlackBackViewParams = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.MATCH_PARENT,
						WindowManager.LayoutParams.MATCH_PARENT,
						typeOverlay,
						WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
						PixelFormat.TRANSLUCENT);

					//Specify the position
					mBlackBackViewParams.gravity = Gravity.CENTER | Gravity.CENTER;
					mBlackBackViewParams.x = 0;
					mBlackBackViewParams.y = 0;

					//expanded view layout
					chatListView = new RelativeLayout(this);
					chatListView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

					//set params
					mExpandedViewParams = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.MATCH_PARENT,
						WindowManager.LayoutParams.MATCH_PARENT,
						typeOverlay,
						WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
						PixelFormat.TRANSLUCENT);

					//Specify the position
					mExpandedViewParams.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
					mExpandedViewParams.x = 0;
					mExpandedViewParams.y = 0;

					//remove layout inflation
					mRemoveView = new RelativeLayout(this);
					mRemoveView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

					//set params
					mRemoveViewParams = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.WRAP_CONTENT,
						typeOverlay,
						WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
						PixelFormat.TRANSLUCENT);

					//Specify the position
					mRemoveViewParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
					mRemoveViewParams.x = 0;
					mRemoveViewParams.y = 0;

					//chat head layout
					mChatHeadView = new RelativeLayout(this);
					mChatHeadView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

					//set params
					mChatHeadViewParams = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.WRAP_CONTENT,
						typeOverlay,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);

					//Specify the chat head position
					mChatHeadViewParams.gravity = Gravity.TOP | Gravity.LEFT;
					mChatHeadViewParams.x = screenWidth-chatHeadWidth;
					mChatHeadViewParams.y = screenHeight/4;

					//////////////////////////////creating interactable stuff and elements//////////////////////////////////
					//some graphics stuff for iconList
					iconProjection = new ImageView(this);
					iconProjection.setImageResource(R.drawable.icon_projection_holo);
					iconProjection.setAlpha(0.7f);
					iconProjectionParams = new WindowManager.LayoutParams(chatHeadWidth * 12, MATCH_PARENT, typeOverlay, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT);
					iconProjectionParams.gravity = Gravity.TOP | Gravity.LEFT;
					iconProjectionParams.x = 0;
					iconProjectionParams.y = 0;

					//icon List as chooser for showing chats
					iconList = new ListView(this);
					iconList.setVerticalScrollBarEnabled(false);
					Drawable divider = new ColorDrawable(Color.TRANSPARENT);
					divider.setAlpha(0);
					iconList.setDivider(divider);
					iconListParams = new WindowManager.LayoutParams(chatHeadWidth * 2, MATCH_PARENT, typeOverlay, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
					iconListParams.gravity = Gravity.TOP | Gravity.LEFT;
					iconListParams.x = 0;
					iconListParams.y = screenHeight / 5;
					firstVisibleIcon = 0;
					totalVisibleIcons = 1;
					extraSize = 1;
					showingChat = false;
					chatInFocus = CHAT_ALL;

					//graphics stuff for chatList

					chatProjectionDown = new LinearLayout(this);
					chatProjectionDownParams = new WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, typeOverlay, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);

					final ImageView chatProjectionDownImg = new ImageView(this);
					chatProjectionDownImg.setImageResource(R.drawable.chat_projection_holo);

					chatProjectionUp = new LinearLayout(this);
					chatProjectionUpParams = new WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, typeOverlay, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);

					final ImageView chatProjectionUpImg = new ImageView(this);
					chatProjectionUpImg.setImageResource(R.drawable.chat_projection_holo);

					chatProjectionUp.addView(chatProjectionUpImg, new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
					chatProjectionDown.addView(chatProjectionDownImg, new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

					final int chatProjectPartsHeightInit = chatListHeight;

					//chatProjection.addView(chatProjectionUp, new LinearLayout.LayoutParams(chatHeadHeight*2, chatProjectPartsHeightInit));
					//chatProjection.addView(chatProjectionDown, new LinearLayout.LayoutParams(chatHeadHeight*2, chatProjectPartsHeightInit));

					//mExpandedView.addView(chatProjection,chatProjectionParams);

					//set the data for chat listView
					chatList = new ListView(this);
					chatList.setBackgroundResource(R.drawable.chat_holo);
					chatList.setItemsCanFocus(true);
					chatList.setDividerHeight(0);
					chatList.setPadding(10, 10, 10, 10);
					final RelativeLayout.LayoutParams chatListParams = new RelativeLayout.LayoutParams(screenWidth, WRAP_CONTENT);
					chatListView.addView(chatList, chatListParams);
					firstVisibleChat = 0;
					totalVisibleChats = 1;
					chatListStates = new ArrayList<Parcelable>();
					chatListStateEmpty = chatList.onSaveInstanceState();
					chatListStates.add(chatListStateEmpty);
					chatListStates.add(chatListStateEmpty);

					final ImageView chatClose = new ImageView(this);
					chatClose.setImageResource(R.drawable.ic_close);
					chatClose.setAlpha(0.5f);
					final RelativeLayout.LayoutParams chatCloseParams = new RelativeLayout.LayoutParams(chatHeadWidth / 2, chatHeadHeight / 2);
					chatListView.addView(chatClose, chatCloseParams);

					//create the holes
					final ImageView[] hole = new ImageView[5];
					ViewGroup.LayoutParams holeParams = new ViewGroup.LayoutParams((int)(xdpi), (int)(ydpi));

					for(int i = 0;i < hole.length;i++)
					{
						hole[i] = new ImageView(this);
						//default shits for animation
						hole[i].setAlpha(0f);//
						hole[i].setX(0);
						hole[i].setY(0);
						mRemoveView.addView(hole[i], holeParams);
					}
					//again, defaults
					hole[0].setScaleX(0f);
					hole[0].setScaleY(0f);

					hole[0].setImageResource(R.drawable.hole0);
					hole[1].setImageResource(R.drawable.hole1);
					hole[2].setImageResource(R.drawable.hole2);
					hole[3].setImageResource(R.drawable.hole3);
					hole[4].setImageResource(R.drawable.hole4);

					//chat head
					chatHead = new ImageView(this);
					chatHead.setImageResource(R.drawable.ic_logo);
					chatHead.setScaleX(isToLeft?1f:-1f);
					chatHead.setScaleX(isToLeft?1f:-1f);
					RelativeLayout.LayoutParams chatHeadParams = new RelativeLayout.LayoutParams(chatHeadWidth, chatHeadHeight);
					mChatHeadView.addView(chatHead, chatHeadParams);

					//the circles showing number of replies
					chatCircleReplies = new TextView(this);
					chatCircleReplies.setBackgroundResource(R.drawable.circle_filled_blue);
					chatCircleReplies.setTextColor(Color.WHITE);
					chatCircleReplies.setTextSize(chatHeadWidth/15);
					chatCircleReplies.setText(" 0");
					RelativeLayout.LayoutParams chatCircleRepliesParams = new RelativeLayout.LayoutParams(chatHeadWidth/4, chatHeadHeight/4);
					chatCircleRepliesParams.setMargins(0, 0, chatHeadWidth - chatHeadWidth/4, chatHeadHeight - chatHeadHeight/4);
					mChatHeadView.addView(chatCircleReplies, chatCircleRepliesParams);

					//the same but with messages
					chatCircleMessages = new TextView(this);
					chatCircleMessages.setBackgroundResource(R.drawable.circle_filled_red);
					chatCircleMessages.setTextColor(Color.WHITE);
					chatCircleMessages.setTextSize(chatHeadWidth/15);
					chatCircleMessages.setText(" 0");
					RelativeLayout.LayoutParams chatCircleMessagesParams = new RelativeLayout.LayoutParams(chatHeadWidth/4, chatHeadHeight/4);
					chatCircleMessagesParams.setMargins(chatHeadWidth - chatHeadWidth/4, 0, 0, chatHeadHeight - chatHeadHeight/4);
					mChatHeadView.addView(chatCircleMessages, chatCircleMessagesParams);

					isExpanded = false;

					//interact and move chat head
					chatHead.setOnTouchListener(new View.OnTouchListener(){

							int initialX;
							int initialY;
							int initialTouchX;
							int initialTouchY;
							final int prevLength = 100;
							int[] xPrev = new int[prevLength];
							int[] yPrev = new int[prevLength];
							int xV = 0;
							int yV = 0;
							int xVMax = 0;
							int yVMax = 0;
							CountDownTimer cTimerV;
							boolean canUpdateVs;
							CountDownTimer cTimerA;
							int justTouchedLengthDelta = chatHeadWidth / 4;
							boolean canAnimatehole = false;

							@Override
							public boolean onTouch(View v, final MotionEvent event)
							{
								//the below is to be executed for every actio
								isShrinked = false;
								if(!showingChat)
								{
									if(mChatHeadViewParams.x + chatHeadWidth / 2 < screenWidth / 2)
										chatHead.setScaleX(1f);
									else
										chatHead.setScaleX(-1f);
								}
								
								switch(event.getAction())
								{

									case MotionEvent.ACTION_DOWN:

										sleepShrinkTimer.cancel();
										sleepTimer.cancel();
										
										//setting the vars
										isTouched = true;
										justTouched = false;
										canUpdateVs = true;
										moving = false;
										isToLeft = mChatHeadViewParams.x + chatHeadWidth / 2 < screenWidth / 2;
										isToTop = mChatHeadViewParams.y + chatHeadHeight / 2 < screenHeight / 2;

										//remember the initial position.
										initialX = mChatHeadViewParams.x;
										initialY = mChatHeadViewParams.y;

										//get the touch location
										initialTouchX = (int) event.getRawX();
										initialTouchY = (int) event.getRawY();

										for(int i = 0;i < xPrev.length;i++)
										{
											xPrev[i] = initialTouchX;
											yPrev[i] = initialTouchY;
										}
										
										chatCircleMessages.setVisibility(View.INVISIBLE);
										chatCircleReplies.setVisibility(View.INVISIBLE);

										chatHead.setX(0);

										chatHead.setAlpha(0.75f);

										if(!showingChat)
										{
										if(isToLeft)
											chatHead.setScaleX(0.90f);
										else
											chatHead.setScaleX(-0.90f);
										}

										chatHead.setScaleY(0.90f);

										new CountDownTimer(ViewConfiguration.getLongPressTimeout(), 1)
										{
											@Override
											public void onTick(long p1)
											{
												// TODO: Implement this method
												if(!isTouched)
													cancel();
											}

											@Override
											public void onFinish()
											{
												// TODO: Implement this method
												if(isTouched && !moving)
												{
													longTouched = true;
													isToLeft = mChatHeadViewParams.x + chatHeadWidth / 2 < screenWidth / 2;
													removing = true;

													if(isExpanded)
													{
														if(showingChat)
															chatListStates.set(chatInFocus+2, chatList.onSaveInstanceState());
														chatListView.setVisibility(View.GONE); //chatList.setAdapter(null);
														isExpanded = false;
														iconList.setVisibility(View.GONE); iconList.setAdapter(null);
														iconProjection.setVisibility(View.GONE);
														chatProjectionUp.setVisibility(View.GONE);
														chatProjectionDown.setVisibility(View.GONE);
														if(showingChat)
														{
															showingChat = false;
															onChatClose(chatInFocus);
														}
														chatHead.setImageResource(R.drawable.ic_logo);
														chatCircleMessages.setVisibility(View.INVISIBLE);
														chatCircleReplies.setVisibility(View.INVISIBLE);
													}

													haptic(10, 255);

													//swell chat head
													if(isToLeft)
														chatHead.setScaleX(1.1f);
													else
														chatHead.setScaleX(-1.1f);

													chatHead.setScaleY(1.1f);

													chatHead.setAlpha(1f);

													mBlackBackView.setVisibility(View.VISIBLE);
													mRemoveView.setVisibility(View.VISIBLE);

													mRemoveView.setAlpha(1f);

													canAnimatehole = true;

													cTimerA = new CountDownTimer(750, delta){

														int holeIndex = -1;
														float alpha = 1f;
														float alpha1 = 0f;

														@Override
														public void onTick(long p1)
														{
															// TODO: Implement this method
															/*for(int i = 0;i < hole.length;i++)
															 {
															 if(hole[i].getAlpha() == 1f)
															 holeIndex = i;
															 }*/
															alpha = (float)p1 / 750f;
															alpha1 = 1f - alpha;

															if(holeIndex > -1 && holeIndex < 4)
															{
																hole[holeIndex].setAlpha(alpha);
																hole[holeIndex + 1].setAlpha(alpha1);
															}
															else

															if(holeIndex == -1)
															{ 
																hole[0].setAlpha(alpha1);

																float scl = Math.min(1f, (1f - (p1 - timeTotal) / 250f));

																hole[0].setScaleX(scl);
																hole[0].setScaleY(scl);

																mBlackBackView.setAlpha(scl / 2f);
															}
															else

															if(holeIndex == 4)
															{
																hole[4].setAlpha(alpha);
																hole[0].setAlpha(alpha1);
															}
														}

														@Override
														public void onFinish()
														{
															// TODO: Implement this method
															if(holeIndex > -1 && holeIndex < 4)
															{
																hole[holeIndex].setAlpha(0f);
																hole[holeIndex + 1].setAlpha(1f);
																holeIndex++;
															}
															else

															if(holeIndex == -1)
															{
																hole[0].setAlpha(1f);
																hole[0].setScaleX(1f);
																hole[0].setScaleY(1f);
																mBlackBackView.setAlpha(0.5f);
																holeIndex = 0;
															}
															else

															if(holeIndex == 4)
															{
																hole[4].setAlpha(0f);
																hole[0].setAlpha(1f);
																holeIndex = 0;
															}

															if(canAnimatehole)
																start();//loop the timer
														}
													};
													cTimerA.start();
													//Toast.makeText(getApplicationContext(),"deltaX : " + String.valueOf(initialTouchX-xPrev[0]) + String.valueOf(
												}
											}
										}.start();

										return true;

									case MotionEvent.ACTION_MOVE:

										int touchX = (int) event.getRawX();
										int touchY = (int) event.getRawY();

										moving = getNum(distance(initialTouchX, initialTouchY, touchX, touchY)) > justTouchedLengthDelta;

										//Calculate the X and Y coordinates of the view.
										mChatHeadViewParams.x = touchX - (initialTouchX - initialX);
										mChatHeadViewParams.y = touchY - (initialTouchY - initialY);

										//Update the layout with new X & Y coordinate
										mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);

										if(!isExpanded)
										{
											if(cTimerV == null)
											{
												cTimerV = new CountDownTimer(chatHeadSensitivity, 1)
												{
													@Override
													public void onTick(long p1)
													{
														// TODO: Implement this method
														if(canUpdateVs)
														{
															int touchXTimer = (int) event.getRawX();
															int touchYTimer = (int) event.getRawY();

															if(touchXTimer != xPrev[0] || touchYTimer != yPrev[0])
															{
																xPrev[0] = touchXTimer;
																yPrev[0] = touchYTimer;

																//xVMax = Math.max(xV, xVMax);
																//yVMax = Math.max(yV, yVMax);

																xV = (int)((xPrev[0] - xPrev[prevLength-1])/2f);
																yV = (int)((yPrev[0] - yPrev[prevLength-1])/2f);
															}

															//last part, let it be here
															for(int i = prevLength-1;i > 0;i--)
															{
																xPrev[i] = xPrev[i - 1];
																yPrev[i] = yPrev[i - 1];
															}
														}
													}

													@Override
													public void onFinish()
													{
														// TODO: Implement this method
														if(canUpdateVs)
															cTimerV.start();
													}
												};
												cTimerV.start();
											}

											if(removing)
												if(new Rect(screenWidth / 2 - hole[0].getWidth() / 2, screenHeight - hole[0].getHeight(), screenWidth / 2 + hole[0].getWidth() / 2, screenHeight).contains(mChatHeadViewParams.x, mChatHeadViewParams.y))
													chatHead.setAlpha(0.5f);
												else
													chatHead.setAlpha(1f);
										}

										else

										if(moving)
										{
											if(showingChat)
												chatListStates.set(chatInFocus+2, chatList.onSaveInstanceState());
											chatListView.setVisibility(View.GONE); //chatList.setAdapter(null);
											isExpanded = false;
											mBlackBackView.setVisibility(View.GONE);
											iconList.setVisibility(View.GONE); iconList.setAdapter(null);
											iconProjection.setVisibility(View.GONE);
											chatProjectionUp.setVisibility(View.GONE);
											chatProjectionDown.setVisibility(View.GONE);
											if(showingChat)
											{
												showingChat = false;
												onChatClose(chatInFocus);
											}
											chatHead.setImageResource(R.drawable.ic_logo);
											chatHead.setAlpha(1f);

											refreshChatCircles();
										}

										return true;

									case MotionEvent.ACTION_UP :
										
										if(cTimerV != null)
										{
											cTimerV.cancel();
											cTimerV = null;
										}
										sleepTimer.start();
										
										//updating vars
										isTouched = false;
										justTouched = getNum(distance(initialX, initialY, mChatHeadViewParams.x, mChatHeadViewParams.y)) <= justTouchedLengthDelta;
										canUpdateVs = false;
										isToLeft = mChatHeadViewParams.x + chatHeadWidth / 2 < screenWidth / 2;
										isToTop = mChatHeadViewParams.y + chatHeadHeight / 2 < screenHeight / 2;
										final boolean willBeToLeft = mChatHeadViewParams.x + chatHeadWidth / 2 + (xV / 6) * stepsTotal < screenWidth / 2;
										final boolean willBeToTop = mChatHeadViewParams.y + chatHeadHeight / 2 + (yV / 6) * stepsTotal < screenHeight / 2;
										
										chatCircleMessages.setVisibility(View.VISIBLE);
										chatCircleReplies.setVisibility(View.VISIBLE);

										//WARNING XXXXXX : Lotta unreasonable calculation below

										final int dp = 4;//Resources.getSystem().getDisplayMetrics().density;

										if(!showingChat)
										{
											if(isToLeft)
												chatHead.setScaleX(1f);
											else
												chatHead.setScaleX(-1f);
										}

										chatHead.setScaleY(1f);

										chatHead.setAlpha(1f);

										if(justTouched && !longTouched && !moving)
										{
											isExpanded = !isExpanded;
											haptic(5, 255);
											if(isExpanded)
											{
												sleepTimer.cancel();
												sleepShrinkTimer.cancel();
												chatCircleMessages.setVisibility(View.INVISIBLE);
												chatCircleReplies.setVisibility(View.INVISIBLE);
												chatHead.setImageResource(R.drawable.ic_close_round_small);
												chatHead.setAlpha(0.5f);
												mChatHeadViewParams.x = willBeToLeft ? 0 : screenWidth - chatHeadWidth;

												if(mChatHeadViewParams.y < chatHeadHeight / 2)
													mChatHeadViewParams.y = chatHeadHeight / 2;
												else
												if(mChatHeadViewParams.y > chatListHeight)
													mChatHeadViewParams.y = chatListHeight;

												int tmpY = chatHeadHeight * Math.round((float)(mChatHeadViewParams.y + chatHeadHeight/2) / (float)chatHeadHeight) - chatHeadHeight/2;
												mChatHeadViewParams.y = tmpY;
												mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);

												iconProjectionParams.gravity = Gravity.TOP | Gravity.LEFT;
												mWindowManager.updateViewLayout(iconProjection, iconProjectionParams);
												iconProjection.setScaleX(0f);
												iconProjection.setScaleY(0f);
												iconProjectionParams.x = willBeToLeft ? -iconProjection.getWidth() / 2 : screenWidth - iconProjection.getWidth() / 2;
												iconProjectionParams.y = mChatHeadViewParams.y + chatHeadHeight/2 - iconProjection.getHeight() / 2;
												mWindowManager.updateViewLayout(iconProjection, iconProjectionParams);

												tmpScaleY = Math.min((float)((senders.size() - chatRemovesCount + extraSize) * 2 * chatHeadHeight) / (float)iconProjection.getWidth(), (float)screenHeight / (float)iconProjection.getWidth());
												tmpScaleX = (float)chatHeadWidth * 4 / (float)iconProjection.getWidth();

												iconListParams.x = mChatHeadViewParams.x;
												mWindowManager.updateViewLayout(iconList, iconListParams);
												iconList.setVisibility(View.GONE); iconList.setAdapter(null);
												iconList.smoothScrollToPosition(0);

												chatListParams.height = chatListHeight;

												chatListParams.setMargins(willBeToLeft ? chatHeadWidth * 2 - chatHeadWidth / 4 + chatHeadWidth / 12 : 0,
																		  chatHeadHeight,
																		  !willBeToLeft ? chatHeadWidth * 2 - chatHeadWidth / 6 : 0,
																		  chatHeadHeight);

												chatCloseParams.setMargins(!willBeToLeft ? (chatListWidth) - chatHeadWidth / 4/*half of close button size*/ + chatHeadWidth / 12/*unreasonable*/: (chatHeadWidth * 2 - chatHeadWidth / 2) + chatHeadWidth / 12,
																		   chatHeadHeight - chatHeadHeight / 4,
																		   0,
																		   0);

												if(willBeToLeft)
												{
													chatProjectionDown.setRotation(-90);
													chatProjectionUp.setRotation(270);
												}
												else
												{
													chatProjectionDown.setRotation(270);
													chatProjectionUp.setRotation(-90);
												}

												final int tmpSize = chatHeadHeight + chatHeadHeight / 2;

												final float tmpScaleDown = (1f - (mChatHeadViewParams.y + chatHeadHeight / 2) / (float)screenHeight);
												final float tmpScaleUp = (mChatHeadViewParams.y + chatHeadHeight / 2) / (float)screenHeight;

												chatProjectionDownImg.setScaleY(isToLeft ? 1 : -1);
												chatProjectionDownImg.setScaleX(-tmpScaleDown);
												chatProjectionDownParams.gravity = Gravity.TOP | Gravity.LEFT;
												chatProjectionDownParams.x = (isToLeft) ? chatHeadWidth / 2 : chatListWidth;
												chatProjectionDownParams.y = mChatHeadViewParams.y + chatHeadHeight / 2 - (int)((float)(tmpSize / 2) * (1f - tmpScaleDown)) - 1;
												chatProjectionDownParams.width = tmpSize;
												chatProjectionDownParams.height = tmpSize;//*(1f-((mChatHeadViewParams.y+chatHeadHeight/2)/(float)screenHeight)));//chatHeadHeight+chatHeadHeight/2;
												mWindowManager.updateViewLayout(chatProjectionDown, chatProjectionDownParams);

												chatProjectionUpImg.setScaleY(isToLeft ? 1 : -1);
												chatProjectionUpImg.setScaleX(tmpScaleUp);
												chatProjectionUpParams.gravity = Gravity.TOP | Gravity.LEFT;
												chatProjectionUpParams.x = (isToLeft) ? chatHeadWidth / 2 : chatListWidth;
												chatProjectionUpParams.y = mChatHeadViewParams.y - chatHeadHeight + (int)((float)(tmpSize / 2) * (1f - tmpScaleUp)) + 1;
												chatProjectionUpParams.width = tmpSize;
												chatProjectionUpParams.height = tmpSize;
												mWindowManager.updateViewLayout(chatProjectionUp, chatProjectionUpParams);

												iconProjection.setVisibility(View.VISIBLE);

												initSize = (int)((float)(mChatHeadViewParams.y + chatHeadHeight/2) / (float)chatHeadHeight);
												finalSize = (int)((float)(screenHeight - mChatHeadViewParams.y - chatHeadHeight/2) / (float)chatHeadHeight)+1;

												new CountDownTimer((int)(timeTotal / 4f), 8){

													@Override
													public void onTick(long p1)
													{
														// TODO: Implement this method
														float factor = 1f - (float)p1/(float)(timeTotal/4f);
														iconProjection.setScaleY(tmpScaleY * factor);
														iconProjection.setScaleX(tmpScaleX * factor);
														//iconProjectionParams.x = willBeToLeft ? -iconProjection.getWidth() / 2 : screenWidth - iconProjection.getWidth() / 2;
														//iconProjectionParams.y = mChatHeadViewParams.y + chatHeadHeight / 2 - iconProjection.getHeight() / 2;
														//mWindowManager.updateViewLayout(iconProjection, iconProjectionParams);
													}

													@Override
													public void onFinish()
													{
														// TODO: Implement this method
														//finalisation of iconProjection
														iconProjection.setScaleY(tmpScaleY);
														iconProjection.setScaleX(tmpScaleX);
														iconProjectionParams.x = willBeToLeft ? -iconProjection.getWidth() / 2 : screenWidth - iconProjection.getWidth() / 2;
														iconProjectionParams.y = mChatHeadViewParams.y + chatHeadHeight / 2 - iconProjection.getHeight() / 2;
														mWindowManager.updateViewLayout(iconProjection, iconProjectionParams);

														final BaseAdapter iconListAdapter = new BaseAdapter(){

															boolean isLessThanSHbyTwo = (getCount() - initSize - finalSize) < (screenHeight / chatHeadHeight) / 2;

															@Override
															public void notifyDataSetChanged()
															{
																if(isExpanded && !showingChat)
																{
																	initSize = (int)((float)(mChatHeadViewParams.y + chatHeadHeight/2) / (float)chatHeadHeight);
																	finalSize = (int)((float)(screenHeight - mChatHeadViewParams.y - chatHeadHeight/2) / (float)chatHeadHeight)+1;
																	isLessThanSHbyTwo = (getCount() - initSize - finalSize) < (screenHeight / chatHeadHeight) / 2;

																	tmpScaleY = Math.min((float)((senders.size() - chatRemovesCount + extraSize) * 2 * chatHeadHeight) / (float)iconProjection.getWidth(), (float)screenHeight / (float)iconProjection.getWidth());
																	tmpScaleX = (float)chatHeadWidth * 4 / (float)iconProjection.getWidth();

																	if(iconProjection.getScaleY() != tmpScaleY || iconProjection.getScaleX() != tmpScaleX)
																	{
																		iconProjection.setScaleY(tmpScaleY);
																		iconProjection.setScaleX(tmpScaleX);
																		iconProjectionParams.x = isToLeft ? -iconProjection.getWidth() / 2 : screenWidth - iconProjection.getWidth() / 2;
																		iconProjectionParams.y = mChatHeadViewParams.y + chatHeadHeight / 2 - iconProjection.getHeight() / 2;
																		mWindowManager.updateViewLayout(iconProjection, iconProjectionParams);
																	}

																	super.notifyDataSetChanged();
																}
															}

															@Override
															public int getCount()
															{
																// TODO: Implement this method
																return initSize + extraSize + (senders.size() - chatRemovesCount) + finalSize;
															}

															@Override
															public Object getItem(int p1)
															{
																// TODO: Implement this method
																return null;
															}

															@Override
															public long getItemId(int p1)
															{
																// TODO: Implement this method
																return 0;
															}

															@Override
															public View getView(final int i, View view, ViewGroup p3)
															{
																// WARNING : calculation below
																final RelativeLayout itemView = new RelativeLayout(getApplicationContext());

																if(i >= initSize && i <= getCount()-finalSize)
																{
																	final boolean isBelow = (i - firstVisibleIcon) * chatHeadHeight >= mChatHeadViewParams.y + chatHeadHeight/2;
																	final boolean isChatInIcons = i >= initSize + extraSize && i < getCount() - finalSize;

																	int chatIconIndexTmp = i - initSize - extraSize;
																	final int chatIconIndex = isChatInIcons? getChatIndexNoRemoves(chatIconIndexTmp, senders.size()-1, -1) : (chatIconIndexTmp == getCount()-finalSize - initSize - extraSize? CHAT_ALL-1 : chatIconIndexTmp);

																	final ImageView icon = new ImageView(getApplicationContext());
																	float scale = 1f;
																	if(isLessThanSHbyTwo)
																		scale = 1f - ((float)(i - initSize - firstVisibleIcon) / (float)(getCount() - initSize - finalSize));
																	else
																		scale = 1f - ((float)(i - initSize - firstVisibleIcon) / (((float)screenHeight / (float)chatHeadHeight) / 2));
																	float iconScale = isBelow ? Math.max(scale, 0f) : Math.max(1f - (scale - 1f), 0f);/*×_×*/
																	icon.setScaleY(iconScale);
																	icon.setScaleX(iconScale);
																	if(isChatInIcons)
																	{
																		if(chatIconIndex < icons.size())
																			icon.setImageIcon(icons.get(chatIconIndex));
																	}else{
																				if(chatIconIndex == CHAT_ALL)
																					icon.setImageResource(R.drawable.inbox);
																				else
																					if(chatIconIndex == CHAT_ALL-1)
																						icon.setImageResource(R.drawable.spam);
																				//more to be added later
																		}
																	if(willBeToLeft)
																		icon.setX(chatHeadWidth - (int)((chatHeadWidth) * (1f - scale) * 2 * (isBelow ? 1f : -1f)));
																	else
																		icon.setX((int)(chatHeadWidth * (1f - scale) * 2 * (isBelow ? 1f : -1f)));

																	icon.setOnClickListener(new View.OnClickListener(){

																			@Override
																			public void onClick(View view)
																			{
																				// TODO: Implement this method

																				//mExpandedView.updateViewLayout(chatProjection, chatProjectionParams);
																				chatListView.updateViewLayout(chatList, chatListParams);
																				chatListView.updateViewLayout(chatClose, chatCloseParams);
																				mWindowManager.updateViewLayout(chatListView, mExpandedViewParams);

																				chatInFocus = chatIconIndex;
																				haptic(5, 255);

																				showingChat = true;
																				chatList.setVisibility(View.INVISIBLE);
																				chatClose.setVisibility(View.INVISIBLE);
																				iconList.setVisibility(View.GONE);
																				iconProjection.setVisibility(View.GONE);
																				if(chatInFocus == CHAT_ALL-1)
																					chatHead.setImageResource(R.drawable.spam);
																				else
																				if(chatInFocus == CHAT_ALL)
																					chatHead.setImageResource(R.drawable.inbox);
																				else
																				{
																					chatHead.setScaleX(1f);
																					chatHead.setImageIcon(icons.get(chatInFocus));
																				}
																				
																				chatHead.setAlpha(1f);

																				//opening of chat(s)
																				mBlackBackView.setVisibility(View.VISIBLE);
																				chatListView.setVisibility(View.VISIBLE);

																				if(mChatHeadViewParams.y + chatHeadHeight / 2 + tmpSize * tmpScaleUp < chatHeadHeight + chatHeadHeight / 2)
																				{
																					chatProjectionUp.setVisibility(View.INVISIBLE);
																					chatProjectionDown.setVisibility(View.VISIBLE);
																				}
																				else
																				if(mChatHeadViewParams.y + chatHeadHeight / 2 + tmpSize * tmpScaleDown > screenHeight - chatHeadHeight - chatHeadHeight / 2)
																				{
																					chatProjectionDown.setVisibility(View.INVISIBLE);
																					chatProjectionUp.setVisibility(View.VISIBLE);
																				}
																				else
																				{
																					chatProjectionUp.setVisibility(View.VISIBLE);
																					chatProjectionDown.setVisibility(View.VISIBLE);
																				}

																				mBlackBackView.setAlpha(0);
																				new CountDownTimer(timeTotal / 2, 8){

																					@Override
																					public void onTick(long p1)
																					{
																						// TODO: Implement this method
																						float factor = 1f - ((float)p1 / (timeTotal / 2f));
																						if(isToLeft)
																						{
																							chatProjectionUpImg.setScaleY(factor);
																							chatProjectionDownImg.setScaleY(factor);
																						}
																						else
																						{
																							chatProjectionUpImg.setScaleY(-(factor));
																							chatProjectionDownImg.setScaleY(-(factor));
																						}

																						mBlackBackView.setAlpha((factor) / 2f);
																					}

																					@Override
																					public void onFinish()
																					{
																						// TODO: Implement this method
																						chatProjectionUpImg.setScaleY(isToLeft ? 1 : -1);
																						chatProjectionDownImg.setScaleY(isToLeft ? 1 : -1);

																						notChatsTop = 0;
																						notChatsBottom = screenHeight/chatHeadHeight - 2 - 1;

																						mBlackBackView.setAlpha(0.5f);

																						chatViewsCount = chatInFocus == CHAT_ALL? (senders.size() - chatRemovesCount) : (chatInFocus == CHAT_ALL-1? chatRemovesCount : 1);
																						replyViews = new ArrayList<TextView>();
																						replyViewSelecteds = new ArrayList<Boolean>();
																						refreshViews = new ArrayList<ImageView>();
																						sendUps = new ArrayList<ImageView>();
																						sendDowns = new ArrayList<ImageView>();
																						sent = new ArrayList<Boolean>();
																						
																						for(int chatViewIndex = 0;chatViewIndex < chatViewsCount;chatViewIndex++)
																						{
																							replyViews.add(null);
																							replyViewSelecteds.add(null);
																							refreshViews.add(null);
																							sendUps.add(null);
																							sendDowns.add(null);
																							sent.add(null);
																						}

																						if(chatList.getAdapter() == null)
																						{
																							final BaseAdapter chatListAdapter = new BaseAdapter(){

																								@Override
																								public void notifyDataSetChanged()
																								{
																									chatViewsCount = chatInFocus == CHAT_ALL? (senders.size() - chatRemovesCount) : (chatInFocus == CHAT_ALL-1? chatRemovesCount : 1);

																									for(int chatViewIndex = 0;chatViewIndex < chatViewsCount;chatViewIndex++)
																									{
																										if(chatViewIndex < replyViews.size())
																										{
																											/*replyViews.set(chatViewIndex, null);
																											replyViewSelecteds.set(chatViewIndex, null);
																											refreshViews.set(chatViewIndex, null);
																											sendUps.set(chatViewIndex, null);
																											sendDowns.set(chatViewIndex, null);
																											sent.set(chatViewIndex, null);*/
																										}
																										else
																										{
																											replyViews.add(null);
																											replyViewSelecteds.add(null);
																											refreshViews.add(null);
																											sendUps.add(null);
																											sendDowns.add(null);
																											sent.add(null);
																										}
																									}

																									super.notifyDataSetChanged();
																								}

																								@Override
																								public int getCount()
																								{
																									if(senders != null)
																										if(chatInFocus == CHAT_ALL)
																											return notChatsTop + (senders.size() - chatRemovesCount)*2 + notChatsBottom + (int)((senders.size() - chatRemovesCount)/chatsPerAd);
																										else
																										if(chatInFocus == CHAT_ALL-1)
																											return notChatsTop + (chatRemovesCount)*2 + notChatsBottom + (int)(chatRemovesCount/chatsPerAd);
																										else
																											return notChatsTop + 2 + notChatsBottom;
																									else
																										return 0;
																								}

																								@Override
																								public Object getItem(int i)
																								{
																									return null;
																								}

																								@Override
																								public long getItemId(int i)
																								{
																									return 0;
																								}

																								@Override
																								public View getView(final int i, View view, ViewGroup viewGroup)
																								{
																									boolean isChatInChats = i >= notChatsTop && i < getCount()-notChatsBottom;
																									final LinearLayout itemView = new LinearLayout(getApplicationContext());
																									itemView.setOrientation(LinearLayout.HORIZONTAL);
																									LinearLayout.LayoutParams itemViewParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
																									itemViewParams.gravity = Gravity.CENTER;
																									itemView.setLayoutParams(itemViewParams);

																									ImageView emptyImg = new ImageView(getApplicationContext());

																									if(i < notChatsTop)
																									{
																										itemView.addView(emptyImg, new LinearLayout.LayoutParams(chatHeadWidth, chatHeadHeight));
																									}
																									else
																									{
																										if(i < getCount()-notChatsBottom)
																										{
																											if(!((i-notChatsTop+1)%(chatsPerAd*2/*sender&message*/+1/*the ad*/)==0))
																											{
																												isChatInChats = true;
																												int adsAboveChat = (int)((i-notChatsTop+1)/(chatsPerAd*2/*sender&message*/+1/*the ad*/));
																												int chatViewIndexTmp = chatInFocus <= CHAT_ALL ? (i-notChatsTop-adsAboveChat)/2 : chatInFocus;
																												final int chatIndex = chatInFocus >= CHAT_ALL? getChatIndexNoRemoves(chatViewIndexTmp, 0, senders.size()) : getChatIndexOfRemoves(chatViewIndexTmp, 0, senders.size());
																												final int chatViewIndex = chatInFocus <= CHAT_ALL? chatViewIndexTmp : 0;

																													if((i - notChatsTop - adsAboveChat) % 2 == 0)
																													{
																														final ImageView icon = new ImageView(getApplicationContext());
																														icon.setImageIcon(icons.get(chatIndex));
																														LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(chatHeadWidth, chatHeadHeight);
																														iconParams.gravity = Gravity.TOP;

																														ImageView removeView = new ImageView(getApplicationContext());
																														removeView.setImageResource(chatInFocus==CHAT_ALL-1? R.drawable.restore : R.drawable.ic_close_round);
																														LinearLayout.LayoutParams removeViewParams = new LinearLayout.LayoutParams(chatHeadWidth/2, chatHeadHeight);

																														removeView.setOnClickListener(new View.OnClickListener(){

																																@Override
																																public void onClick(View view)
																																{
																																	// TODO: Implement this method
																																	/*if(chatInFocus == CHAT_ALL)
																																	{
																																		replyViews.remove(chatViewIndex);
																																		replyViewSelecteds.remove(chatViewIndex);
																																		refreshViews.remove(chatViewIndex);
																																		sendUps.remove(chatViewIndex);
																																		sendDowns.remove(chatViewIndex);
																																		sent.remove(chatViewIndex);
																																		chatViewsCount--;
																																	}
																																	else
																																	{
																																		replyViews.set(0, null);
																																		replyViewSelecteds.set(0, null);
																																		refreshViews.set(0, null);
																																		sendUps.set(0, null);
																																		sendDowns.set(0, null);
																																		sent.set(0, null);
																																	}*/
																																	haptic(5, 192);
																																	if(chatInFocus == CHAT_ALL-1)
																																	{
																																		if(chatNotSpams.get(chatIndex) != null)
																																		{
																																			final AlertDialog spamDialog = new AlertDialog.Builder(ChatHeadService.this)
																																			.setCancelable(true)
																																			.setTitle("Not Spam?")
																																			.setMessage(Html.fromHtml("<font color='#FFFFFF'>Is this message not spam?</font>"))
																																			.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
																																				@Override
																																				public void onClick(DialogInterface p1, int p2)
																																				{
																																					haptic(5, 192);
																																					if(chatSpams.get(chatIndex))
																																						chatSpams.set(chatIndex, false);
																																					else
																																						chatNotSpams.set(chatIndex, true);
																																				}
																																			})
																																			.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
																																				@Override
																																				public void onClick(DialogInterface p1, int p2)
																																				{
																																					haptic(5, 192);
																																				}
																																			})
																																			.create();
																																			spamDialog.getWindow().setType(typeOverlay);
																																			spamDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
																																			spamDialog.setOnShowListener(new DialogInterface.OnShowListener() {
																																                @Override
																																                public void onShow(DialogInterface arg0) {
																																                    int titleId = getResources().getIdentifier("alertTitle", "id", "android");
																																	                TextView title = (TextView) spamDialog.findViewById(titleId);
																																					title.setTextColor(Color.parseColor("#01def9"));
																																		            Button nbutton = spamDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
																																			        nbutton.setTextColor(Color.parseColor("#00dff9"));
																																			        Button pbutton = spamDialog.getButton(DialogInterface.BUTTON_POSITIVE);
																																			        pbutton.setTextColor(Color.parseColor("#00dff9"));
																																                }
																																			});
																																			spamDialog.show();
																																		}
																																		chatRemoves.set(chatIndex, false);
																																		chatRemovesCount--;
																																		onChatDataChange();
																																	}
																																	else
																																	{
																																		final AlertDialog spamDialog = new AlertDialog.Builder(ChatHeadService.this)
																																		.setCancelable(true)
																																		.setTitle("Spam?")
																																		.setMessage(Html.fromHtml("<font color='#FFFFFF'>Ignore this type of message next time?</font>"))
																																		.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
																																			@Override
																																			public void onClick(DialogInterface p1, int p2)
																																			{
																																				haptic(5, 192);
																																				chatSpams.set(chatIndex, true);
																																				chatNotSpams.set(chatIndex, false);
																																			}
																																		})
																																		.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
																																			@Override
																																			public void onClick(DialogInterface p1, int p2)
																																			{
																																				haptic(5, 192);
																																			}
																																		})
																																		.create();
																																		spamDialog.getWindow().setType(typeOverlay);
																																		spamDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
																																		spamDialog.setOnShowListener(new DialogInterface.OnShowListener() {
																															                @Override
																															                public void onShow(DialogInterface arg0) {
																															                    int titleId = getResources().getIdentifier("alertTitle", "id", "android");
																																                TextView title = (TextView) spamDialog.findViewById(titleId);
																																				title.setTextColor(Color.parseColor("#01def9"));
																																	            Button nbutton = spamDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
																																		        nbutton.setTextColor(Color.parseColor("#00dff9"));
																																		        Button pbutton = spamDialog.getButton(DialogInterface.BUTTON_POSITIVE);
																																		        pbutton.setTextColor(Color.parseColor("#00dff9"));
																															                }
																																		});
																																		spamDialog.show();
																																		chatRemoves.set(chatIndex, true);
																																		chatRemovesCount++;
																																		onChatDataChange();
																																	}
																																	refreshNotification();
																																}
																															});

																														if(isToLeft && chatInFocus <= CHAT_ALL)
																															itemView.addView(removeView, removeViewParams);

																														final LinearLayout text = new LinearLayout(getApplicationContext());
																														text.setOrientation(LinearLayout.VERTICAL);
																														final LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(chatListWidth - iconParams.width - removeViewParams.width, WRAP_CONTENT);
																														final TextView senderText = new TextView(getApplicationContext());
																														final TextView messageText = new TextView(getApplicationContext());
																														final LinearLayout.LayoutParams senderTextParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																														final LinearLayout.LayoutParams messageTextParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

																														if(senders != null)
																															if(senders.get(chatIndex) != null)
																															{
																																senderText.setTextColor((int)senderColors.get(chatIndex));
																																senderText.setTypeface(Typeface.DEFAULT_BOLD);
																																senderText.setTextIsSelectable(false);
																																senderText.setLinkTextColor(Color.rgb(0, 229, 255));
																																senderText.setLinksClickable(true);
																																senderText.setText(senders.get(chatIndex));
																															}
																															else
																																senderText.setText("null");
																														else
																															senderText.setText("");

																														if(messages != null)
																															if(messages.get(chatIndex) != null)
																															{
																																if(seens.get(chatIndex) == null || !seens.get(chatIndex))
																																{
																																	messageText.setTextColor(Color.WHITE);
																																	messageText.setTypeface(Typeface.DEFAULT_BOLD);
																																}
																																else
																																{
																																	messageText.setTextColor(Color.LTGRAY);
																																}
																																messageText.setTextIsSelectable(true);
																																messageText.setLinkTextColor(Color.rgb(0, 229, 255));
																																messageText.setLinksClickable(true);
																																messageText.setText(messages.get(chatIndex));
																															}
																															else
																																messageText.setText("null");
																														else
																															messageText.setText("");

																														View.OnClickListener senderClickListener = new View.OnClickListener(){

																																@Override
																																public void onClick(View view)
																																{
																																	// TODO: Implement this method
																																	haptic(5, 192);
																																	if(!pkgs.get(chatIndex).equals(appPkg))
																																	{
																																		requestProfileIntents(chatIndex, ids.get(chatIndex));
																																		if(showingChat)
																																			chatListStates.set(chatInFocus+2, chatList.onSaveInstanceState());
																																		chatListView.setVisibility(View.GONE); //chatList.setAdapter(null);
																																		isExpanded = false;
																																		mBlackBackView.setVisibility(View.GONE);
																																		iconList.setVisibility(View.GONE); iconList.setAdapter(null);
																																		iconProjection.setVisibility(View.GONE);
																																		chatProjectionUp.setVisibility(View.GONE);
																																		chatProjectionDown.setVisibility(View.GONE);
																																		if(showingChat)
																																		{
																																			showingChat = false;
																																			onChatClose(chatInFocus);
																																		}
																																		chatHead.setImageResource(R.drawable.ic_logo);
																																		chatHead.setAlpha(1f);

																																		refreshChatCircles();
																																	}
																																}
																															};

																														senderText.setOnClickListener(senderClickListener);

																														text.addView(senderText, senderTextParams);
																														text.addView(messageText, messageTextParams);

																														if(chatIndex < imgs.size()-1)
																															if(imgs.get(chatIndex) != null)
																															{
																																ImageView img = new ImageView(getApplicationContext());
																																img.setImageBitmap(imgs.get(chatIndex));

																																text.addView(img);
																															}

																														itemView.addView(icon, iconParams);
																														itemView.addView(text, textParams);

																														if(!isToLeft && chatInFocus <= CHAT_ALL)
																															itemView.addView(removeView, removeViewParams);

																														itemView.setOnClickListener(senderClickListener);
																													}
																													else
																													{
																														seens.set(chatIndex, seens.get(chatIndex) != null);

																														try{
																															sent.set(chatViewIndex, sendButtonClicks.get(chatIndex) || autoSends.get(chatIndex));
																														}catch(Exception e){
																															chatViewsCount = chatInFocus == CHAT_ALL? (senders.size() - chatRemovesCount) : (chatInFocus == CHAT_ALL-1? chatRemovesCount : 1);
																															replyViews = new ArrayList<TextView>();
																															replyViewSelecteds = new ArrayList<Boolean>();
																															refreshViews = new ArrayList<ImageView>();
																															sendUps = new ArrayList<ImageView>();
																															sendDowns = new ArrayList<ImageView>();
																															sent = new ArrayList<Boolean>();
																															
																															for(int chatViewIndexx = 0;chatViewIndexx < chatViewsCount;chatViewIndexx++)
																															{
																																replyViews.add(null);
																																replyViewSelecteds.add(null);
																																refreshViews.add(null);
																																sendUps.add(null);
																																sendDowns.add(null);
																																sent.add(null);
																															}

																															sent.set(chatViewIndex, sendButtonClicks.get(chatIndex) || autoSends.get(chatIndex));
																														}
																														final LinearLayout replyLayout = new LinearLayout(getApplicationContext());
																														replyLayout.setOrientation(LinearLayout.VERTICAL);
																														LinearLayout.LayoutParams replyLayoutParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																														replyLayout.setLayoutParams(itemViewParams);
																														final TextView replyView = !sent.get(chatViewIndex)? new EditText(getApplicationContext()) : new TextView(getApplicationContext());
																														replyView.setText(replies.get(chatIndex));
																														final CheckBox sendDataView = new CheckBox(getApplicationContext());
																														//sendDataView.setBackgroundColor(Color.parseColor("#00dff9"));
																														sendDataView.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));//setButtonTintList is accessible directly on API>19
																														sendDataView.setChecked(true);
																														sendDataView.setTextColor(Color.LTGRAY);
																														sendDataView.setText("save reply");
																														sendDataView.setVisibility(View.GONE);

																														String replyStr = replyView.getText().toString();

																														//send button/image
																														final RelativeLayout sendView = new RelativeLayout(getApplicationContext());
																														LinearLayout.LayoutParams sendViewParams = new LinearLayout.LayoutParams(chatHeadWidth, chatHeadHeight);
																														final ImageView sendUp = new ImageView(getApplicationContext());
																														final ImageView sendDown = new ImageView(getApplicationContext());
																														RelativeLayout.LayoutParams sendUpParams = new RelativeLayout.LayoutParams(chatHeadWidth, chatHeadHeight);
																														RelativeLayout.LayoutParams sendDownParams = new RelativeLayout.LayoutParams(chatHeadWidth, chatHeadHeight);
																														
																														Button emoji = new Button(getApplicationContext());
																														emoji.setText("ツ");
																														emoji.setTextColor(Color.parseColor("#00dff9"));
																														emoji.setBackgroundResource(R.drawable.chat_holo);
																														LinearLayout.LayoutParams emojiParams = new LinearLayout.LayoutParams(chatHeadWidth/2, chatHeadHeight/2);
																														emoji.setOnClickListener(new View.OnClickListener() {
																															@Override
																															public void onClick(View v) {
																																/*PopupMenu popupMenu = new PopupMenu(AppActivity.this, emoji);
																																popupMenu.getMenu().add(":--)");
																																popupMenu.getMenu().add(":--|");
																																popupMenu.getMenu().add(":--(");
																																popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
																																	@Override
																																	public boolean onMenuItemClick(MenuItem item) {
																																		emoji.setText(item.getTitle().toString()+" ↓");
																																		return false;
																																	}
																																});
																																popupMenu.show();*/
																																haptic(3, 192);
																																AlertDialog.Builder builder = new AlertDialog.Builder(ChatHeadService.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
																																									.setOnDismissListener(new DialogInterface.OnDismissListener() {
																																					                    @Override
																																					                    public void onDismiss(DialogInterface dialogInterface) {
																																					                       haptic(5, 192);
																																					                    }
																																					                });
																																LinearLayout emojiDialogLayout = new LinearLayout(getApplicationContext());
																																emojiDialogLayout.setOrientation(LinearLayout.VERTICAL);
																																emojiDialogLayout.setPadding(15, 15, 15, 15);
																																builder.setView(emojiDialogLayout);
																																builder.setCancelable(true);
																																AlertDialog dialog = builder.create();
																																dialog.getWindow().setType(typeOverlay);
																																dialog.getWindow().setBackgroundDrawableResource(R.drawable.chat_holo);
																																ScrollView scrollView = new ScrollView(getApplicationContext());
																																LinearLayout.LayoutParams scrollViewParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
																																LinearLayout emojisLayout = new LinearLayout(getApplicationContext());
																																emojisLayout.setOrientation(LinearLayout.VERTICAL);
																																scrollView.addView(emojisLayout);
																																TabLayout tabLayout = new TabLayout(new ContextThemeWrapper(getApplicationContext(), R.style.DialogTheme));
																																tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
																																tabLayout.setTabTextColors(Color.parseColor("#009bbd"), Color.parseColor("#01def9"));
																																LinearLayout.LayoutParams tabLayoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
																																for(int e=0;e<emojis.length;e++)
																																	tabLayout.addTab(tabLayout.newTab().setText(emojis[e][0]));  
																														        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
																																tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {  
																														            @Override  
																														            public void onTabSelected(TabLayout.Tab tab) {
																														            	haptic(5, 128);
																																		scrollView.scrollTo(0, 0);
																																		emojisLayout.removeAllViews();
																																		LinearLayout ttabLayout = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
																															            TextView tabTextView = (TextView) ttabLayout.getChildAt(1);
																															            tabTextView.setTypeface(tabTextView.getTypeface(), Typeface.BOLD);
																														                int e = tab.getPosition();
																																		String[] emojiss = emojis[e][1].split("  ");
																																		for(int r=0;r<emojiss.length;r++)
																																		{
																																				Button button = new Button(getApplicationContext());
																																				button.setBackgroundColor(Color.TRANSPARENT);
																																				button.setText(emojiss[r]);
																																				button.setTextColor(Color.WHITE);
																																				button.setOnClickListener(new View.OnClickListener()
																																				{
																																					@Override
																																					public void onClick(View view)
																																					{
																																						replyView.setText(replyView.getText().toString()+button.getText().toString());
																																						dialog.dismiss();
																																					}
																																				});
																																			emojisLayout.addView(button, new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
																																			if(r<emojiss.length-1)
																																			{
																																				View viewDivider = new View(getApplicationContext());
																																			    viewDivider.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 5));
																																			    viewDivider.setBackgroundColor(Color.parseColor("#008392"));
																																			    emojisLayout.addView(viewDivider);
																																			}
																																		}
																														            }  
																														  
																														            @Override  
																														            public void onTabUnselected(TabLayout.Tab tab) {  
																														  				LinearLayout ttabLayout = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
																															            TextView tabTextView = (TextView) ttabLayout.getChildAt(1);
																															            tabTextView.setTypeface(null, Typeface.NORMAL);
																														            }  
																														  
																														            @Override  
																														            public void onTabReselected(TabLayout.Tab tab) {  
																														  
																														            }  
																														        });
																																int e = 0;
																																String[] emojiss = emojis[e][1].split("  ");
																																for(int r=0;r<emojiss.length;r++)
																																{
																																	Button button = new Button(getApplicationContext());
																																	button.setBackgroundColor(Color.TRANSPARENT);
																																	button.setText(emojiss[r]);
																																	button.setTextColor(Color.WHITE);
																																	button.setOnClickListener(new View.OnClickListener()
																																	{
																																		@Override
																																		public void onClick(View view)
																																		{
																																			replyView.setText(replyView.getText().toString()+button.getText().toString());
																																			dialog.dismiss();
																																		}
																																	});
																																	emojisLayout.addView(button, new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
																																	if(r<emojiss.length-1)
																																	{
																																		View viewDivider = new View(getApplicationContext());
																																	    viewDivider.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 5));
																																	    viewDivider.setBackgroundColor(Color.parseColor("#008392"));
																																	    emojisLayout.addView(viewDivider);
																																	}
																																}
																																emojiDialogLayout.addView(tabLayout, tabLayoutParams);
																																emojiDialogLayout.addView(scrollView, scrollViewParams);

																																dialog.show();
																															}
																														});
										
																														final float tmpScaleYSend = Math.min((i >= firstVisibleChat)? (float) (i - firstVisibleChat) / (float) ((totalVisibleChats != 0)? totalVisibleChats : (screenHeight - chatHeadHeight*2)/chatHeadHeight) : 0f, 1f);
																														//Toast.makeText(getApplicationContext(), String.valueOf(tmpScaleYSend), Toast.LENGTH_SHORT).show();
																														
																														final CountDownTimer sendMoveTimer = new CountDownTimer(timeTotal/4, delta){

																															@Override
																															public void onTick(long p1)
																															{
																																// TODO: Implement this method
																																float factor = ((float)p1/(float)timeTotal/4);
																																
																																if(!sent.get(chatViewIndex))
																																{
																																	float tmpScale = (1f - tmpScaleYSend) + (chatHeadHeight/2)*(reverse? factor : (1f - factor));
																																	sendUp.setY((chatHeadHeight / 20) * tmpScale);
																																	sendDown.setScaleY(tmpScale);
																																}
																																else
																																{
																																	float tmpY = (chatHeadHeight/2)*(reverse? factor : (1f - factor));
																																	sendDown.setY(tmpY);
																																	sendUp.setY(-tmpY);
																																}
																															}

																															@Override
																															public void onFinish()
																															{
																																// TODO: Implement this method
																																if(!sent.get(chatViewIndex))
																																{
																																	float tmpScale = (1f - tmpScaleYSend) + (chatHeadHeight/2) * (reverse? 0 : 1);
																																	sendUp.setY((chatHeadHeight / 20) * tmpScale);
																																	sendDown.setScaleY(tmpScale);
																																}
																																else
																																{
																																	float tmpY = ((chatHeadHeight/2) * (reverse? 0 : 1));
																																	sendDown.setY(tmpY);
																																	sendUp.setY(-tmpY);
																																}
																																if(reverse)
																																{
																																	sendDown.setScaleY(1f);
																																	notifyDataSetChanged();

																																	if(!directReply && !senders.get(chatViewIndex).equals("Bot"))
																																	{
																																							if(showingChat)
																																								chatListStates.set(chatInFocus+2, chatList.onSaveInstanceState());
																																							chatListView.setVisibility(View.GONE); //chatList.setAdapter(null);
																																							isExpanded = false;
																																							mBlackBackView.setVisibility(View.GONE);
																																							iconList.setVisibility(View.GONE); iconList.setAdapter(null);
																																							iconProjection.setVisibility(View.GONE);
																																							chatProjectionUp.setVisibility(View.GONE);
																																							chatProjectionDown.setVisibility(View.GONE);
																																							if(showingChat)
																																							{
																																								showingChat = false;
																																								onChatClose(chatInFocus);
																																							}
																																							chatHead.setImageResource(R.drawable.ic_logo);
																																							chatHead.setAlpha(1f);

																																							refreshChatCircles();
																																	}
																																}
																															}
																														};
																														
																														sendView.setOnTouchListener(new View.OnTouchListener(){
																																
																																@Override
																																public boolean onTouch(View view, MotionEvent event)
																																{
																																	// TODO: Implement this method
																																	if(event.getAction() != MotionEvent.ACTION_MOVE)
																																	{
																																		if(event.getAction() == MotionEvent.ACTION_DOWN
																																		   )//|| view.isPressed())
																																		{
																																			sent.set(chatViewIndex, sendButtonClicks.get(chatIndex) || autoSends.get(chatIndex));
																																			String replyStr = !sent.get(chatViewIndex)? replyView.getText().toString() : MESSAGE_EMPTY;

																																			if(!(replyStr.equals(PENDING) || replyStr.equals(LOADING)))
																																			{
																																			haptic(3, 192);
																																			sendMoveTimer.cancel();
																																			//sendUp.setY((chatHeadHeight / 20) * (1f - tmpScaleYSend));
																																			//sendDown.setScaleY(1f - tmpScaleYSend);
																																			reverse = false;
																																			sendMoveTimer.start();
																																			}
																																			else
																																			{
																																				reverse = true;
																																				sendMoveTimer.onFinish();
																																			}
																																		}
																																		else
																																		if(event.getAction() == MotionEvent.ACTION_UP
																																		   || !view.isPressed())
																																		{
																																			sendMoveTimer.cancel();
																																			reverse = true;

																																			if(event.getAction() == MotionEvent.ACTION_UP)
																																			{
																																				String replyStr = replyView.getText().toString();
																																				
																																				if(notSents > 1)
																																				{
																																					Toast.makeText(getApplicationContext(), String.valueOf(notSents) + " replies not sent", Toast.LENGTH_LONG).show();
																																					notSents = 0;
																																				}
																																				else
																																				if(notSents == 1)
																																				{
																																						clipboardC.setPrimaryClip(ClipData.newPlainText("reply", replyStr));
																																						Toast.makeText(getApplicationContext(), "reply not sent, copied to clipboard", Toast.LENGTH_SHORT).show();
																																						notSents = 0;
																																				}

																																				if(!(replyStr.equals(PENDING) || replyStr.equals(LOADING)))
																																				{
																																					haptic(5, 192);
																																					sendMoveTimer.start();
																																				}
																																			}
																																			else
																																			{
																																				sendMoveTimer.onFinish();
																																			}
																																		}
																																	}
																																	else
																																	{
																																		if(!((int)event.getX() > 0 && (int)event.getY() > 0 && (int)event.getX() < view.getWidth() && (int)event.getY() < view.getHeight()))
																																		{
																																			reverse = true;
																																			sendMoveTimer.onFinish();
																																		}
																																	}
																																	
																																	return false;
																																}
																															});
																														
																														sendView.setOnLongClickListener(new View.OnLongClickListener(){

																																@Override
																																public boolean onLongClick(View view)
																																{
																																	// TODO: Implement this method
																																	reverse = true;
																																	sendMoveTimer.onFinish();
																																	return false;
																																}
																														});
																														
																														sendView.setOnClickListener(new View.OnClickListener(){ 

																																@Override
																																public void onClick(View view)
																																{
																																	// TODO: Implement this method
																																	sent.set(chatViewIndex, sendButtonClicks.get(chatIndex) || autoSends.get(chatIndex));
																																	String replyStr = !sent.get(chatViewIndex)? replyView.getText().toString() : MESSAGE_EMPTY;
																																	
																																	if(!(replyStr.equals(PENDING) || replyStr.equals(LOADING)))
																																	{
																																	if(!sent.get(chatViewIndex))
																																	{
																																		if(!replyStr.trim().equals(replies.get(chatIndex).trim())
																																		   && !(replyStr.equals(PENDING) || replyStr.equals(LOADING)))
																																		{
																																			if(sendDataView.isChecked())
																																			{
																																				toLearn.add(new String[]{messages.get(chatIndex), replyStr});
																																				loaded = false;
																																			}
																																		}
																																	}

																																		directReply = false;
																																		if(!pkgs.get(chatIndex).equals(appPkg))
																																		{
																																			if(!sent.get(chatViewIndex)
																																			   && ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
																																			   && !ids.get(chatIndex).equals(""))
																																			   && requestReplyIntents(chatIndex, senders.get(chatIndex), messages.get(chatIndex), replyStr, ids.get(chatIndex)))
																																			{
																																				directReply = true;
																																			}	
																																			else
																																			{
																																				boolean sentTmp = false;
																																				try
																																				{
																																					if(sendTos.get(chatIndex).equals(""))
																																						sendTos.set(chatIndex, getSendTo(senders.get(chatIndex), pkgs.get(chatIndex)));

																																					if(!sendTextMessage(replyStr, pkgs.get(chatIndex), sendTos.get(chatIndex)))
																																						notSents++;
																																					else
																																						sentTmp = true;
																																				}catch(Exception e)
																																				{
																																					Toast.makeText(getApplicationContext(), "failed to find contact", Toast.LENGTH_SHORT).show();
																																					if(!sendTextMessage(replyStr, pkgs.get(chatIndex), null))
																																						notSents++;
																																					else
																																						sentTmp = true;
																																				}finally
																																				{
																																					if(!sent.get(chatViewIndex))
																																					{
																																						if(sentTmp)
																																						{
																																							clipboardC.setPrimaryClip(ClipData.newPlainText("reply", replyStr));
																																							Toast.makeText(getApplicationContext(), "reply copied to clipboard", Toast.LENGTH_SHORT).show();

																																							sendButtonClicks.set(chatIndex, true);
																																							sent.set(chatViewIndex, true);
																																							refreshNotification();
																																						}
																																					}
																																				}
																																			}
																																		}
																																		else
																																		{
																																			Intent botReplyIntent = new Intent(getApplicationContext(), ChatHeadService.class)
																																				.putExtra("type", "update")
																																				.putExtra("data", new String[][]{{"Bot"}, { bot.getReply(replyStr)[0] }})
																																				.putExtra("icon", botIcon)
																																				.putExtra("id", String.valueOf(random.nextInt()))
																																				//.putExtra("channel", "channel")
																																				.putExtra("bIcon", (Icon)null)
																																				.putExtra("image", (Bitmap)null)
																																				.putExtra("pkg", appPkg);

																																			startService(botReplyIntent);
																																		}
																																	}
																																	else
																																	{
																																		Toast.makeText(getApplicationContext(), "reply not loaded", Toast.LENGTH_SHORT).show();
																																	}
																																	/*if(sendDataView.isChecked())
																																	{
																																		new Thread(){
																																            public void run(){
																																                try{
																																                    Socket s=new Socket(InetAddress.getByName("103.107.115.85"),9000);
																																                    DataInputStream din=new DataInputStream(s.getInputStream());
																																                    String portStr = din.readUTF();
																																                    din.close();
																																                    s.close();
																																                    
																																                    Socket s1=new Socket(InetAddress.getByName("103.107.115.85"),Integer.parseInt(portStr));  
																																                    DataOutputStream d1out=new DataOutputStream(s1.getOutputStream());
																																                    d1out.writeUTF("learn:"+messages.get(chatIndex)+"  ,   "+replyStr);  
																																                    d1out.flush();
																																                    d1out.close();
																																                    s1.close();
																																                }catch(Exception e){Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();}
																																            }
																																        }.start();
																																    }*/
																																	sendDataView.setVisibility(View.GONE);
																																}
																															});

																														sendView.addView(sendUp, sendUpParams);
																														sendView.addView(sendDown, sendDownParams);

																														if(!sent.get(chatViewIndex))
																														{
																															replyView.setTextColor(Color.WHITE);
																															replyView.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
																															replyView.setOnFocusChangeListener(new View.OnFocusChangeListener(){

																																	@Override
																																	public void onFocusChange(View view, boolean hasFocus)
																																	{
																																		// TODO: Implement this method
																																		String replyStr = replyView.getText().toString();

																																		if(replies.get(chatIndex).equals(replyStr))
																																		{
																																			if(hasFocus)
																																			{
																																				sendDataView.setVisibility(View.VISIBLE);
																																				if(replyViewSelecteds.get(chatViewIndex) != null)
																																				{
																																					if(!replyViewSelecteds.get(chatViewIndex))
																																					{
																																						for(int chatViewIndexTmp = 0;chatViewIndexTmp < chatViewsCount;chatViewIndexTmp++)
																																						{
																																							replyViewSelecteds.set(chatViewIndexTmp, null);
																																						}

																																						((EditText)replyView).selectAll();
																																						replyViewSelecteds.set(chatViewIndex, true);
																																					}
																																				}
																																				else
																																				{
																																					replyViewSelecteds.set(chatViewIndex, false);
																																				}
																																			}else
																																			{
																																				if(replyViewSelecteds.get(chatViewIndex))
																																				{
																																					replyViewSelecteds.set(chatViewIndex, null);
																																					sendDataView.setVisibility(View.GONE);
																																					replies.set(chatIndex, replyStr);
																																				}
																																			}
																																		}
																																	}
																																});
																															//this params may be updated below
																															final LinearLayout.LayoutParams replyViewParams = new LinearLayout.LayoutParams(chatListWidth - sendViewParams.width-emojiParams.width, WRAP_CONTENT);
																															final LinearLayout.LayoutParams sendDataViewParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

																															final ImageView refreshView = new ImageView(getApplicationContext());
																															refreshView.setImageResource(R.drawable.ic_refresh);

																															refreshView.setOnTouchListener(new View.OnTouchListener(){

																																	boolean reverse = false;
																																	CountDownTimer refreshRotateTimer = new CountDownTimer(timeTotal/3, delta){

																																		@Override
																																		public void onTick(long p1)
																																		{
																																			// TODO: Implement this method
																																			refreshView.setRotation(reverse?
																																									refreshView.getRotation()*(float)Math.sin((float)p1/(float)(timeTotal/3)*(float)Math.PI/2)
																																									: -180f*(float)Math.cos((float)p1/(float)(timeTotal/3)*(float)Math.PI/2));
																																		}

																																		@Override
																																		public void onFinish()
																																		{
																																			// TODO: Implement this method
																																			refreshView.setRotation(reverse? 0f : -180f);
																																		}
																																	};

																																	@Override
																																	public boolean onTouch(View view, MotionEvent event)
																																	{
																																		// TODO: Implement this method
																																		if(event.getAction() != MotionEvent.ACTION_MOVE)
																																		{
																																			if(chatLoading != chatIndex)
																																			{
																																				if(event.getAction() == MotionEvent.ACTION_DOWN)
																																				{
																																					haptic(3, 192);
																																					refreshRotateTimer.cancel();
																																					refreshView.setRotation(0f);
																																					reverse = false;
																																					refreshRotateTimer.start();
																																				}
																																				else
																																				if(event.getAction() == MotionEvent.ACTION_UP
																																				   || !view.isPressed())
																																				{
																																					haptic(5, 192);
																																					refreshRotateTimer.cancel();
																																					reverse = true;
																																					refreshRotateTimer.start();
																																				}
																																			}
																																		}
																																		return false;
																																	}
																																});

																															refreshView.setOnClickListener(new View.OnClickListener(){

																																	@Override
																																	public void onClick(View view)
																																	{
																																		// TODO: Implement this method
																																		if(chatLoading != chatIndex)
																																		{
																																			String rly  = bot.getReply(messages.get(chatIndex))[0];

																																			replies.set(chatIndex, rly);
																																			replyView.setText(replies.get(chatIndex));
																																		}
																																	}
																																});

																															LinearLayout.LayoutParams refreshViewParams = new LinearLayout.LayoutParams(chatHeadWidth/2, chatHeadHeight/2);
																															
																															if(replyStr.trim().equals("") || replyStr.equals(PENDING))
																																refreshView.setVisibility(View.GONE);
																															else
																															{
																																replyViewParams.width -= refreshViewParams.width;
																																
																																if(replyStr.equals(LOADING) && chatLoading == chatIndex)
																																{
																																	refreshLoadingTimer.cancel();
																																	chatLoadingViewIndex = chatViewIndex;
																																	refreshLoadingTimer.start();
																																}
																															}

																															refreshViews.set(chatViewIndex, refreshView);

																															//ImageView buttonN = null;
																															//LinearLayout.LayoutParams buttonNParams = null;

																																//notification button like : like, mark as read
																																Icon bIcon = buttonIcons.get(chatIndex);
																																String bText = buttonTexts.get(chatIndex);

																																final View buttonN = (bIcon!=null)? new ImageView(getApplicationContext()) : new Button(getApplicationContext(), null,android.R.attr.buttonBarButtonStyle);
																																if(bIcon!=null)
																																	((ImageView)buttonN).setImageIcon(bIcon);
																																else
																																	if(bText!=null)
																																	{
																																		((Button)buttonN).setTextColor(Color.CYAN);
																																		((Button)buttonN).setText(bText);
																																	}

																																//button.setColorFilter(Color.BLUE);
																																final LinearLayout.LayoutParams buttonNParams = (bIcon!=null)? new LinearLayout.LayoutParams(chatHeadWidth/2, chatHeadHeight/2) : new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																																buttonN.setOnClickListener(new View.OnClickListener(){

																																		@Override
																																		public void onClick(View p1)
																																		{
																																			// TODO: Implement this method
																																			String actTitle = buttonTexts.get(chatIndex);
																																			String pkg = pkgs.get(chatIndex);

																																			if(!(actTitle.contains("like") || actTitle.contains("thumb") || pkg.contains("insta") || pkg.contains("viber") || actTitle.contains("heart") || actTitle.contains("love")))
																																			{
																																				requestButtonIntents(ids.get(chatIndex));
																																				itemView.removeView(buttonN);
																																				
																																				buttonIcons.set(chatIndex, null);
																																						buttonTexts.set(chatIndex, null);

																																				replyViewParams.width += buttonNParams.width;
																																				replyLayout.updateViewLayout(replyView, replyViewParams);
																																			}
																																		}
																																	});

																																replyViewParams.width -= buttonNParams.width;
																																//buttonN = button;
																																//buttonNParams = buttonParams;

																															buttonN.setOnTouchListener(new View.OnTouchListener(){

																																	boolean reverse = false;
																																	String actTitle = buttonTexts.get(chatIndex);
																																	String pkg = pkgs.get(chatIndex);

																																	CountDownTimer refreshRotateTimer = new CountDownTimer(timeTotal/8, delta){

																																		@Override
																																		public void onTick(long p1)
																																		{
																																			// TODO: Implement this method
																																			if(pkg.contains("insta") || pkg.contains("viber") || actTitle.contains("heart") || actTitle.contains("love"))
																																			{
																																				((ImageView)buttonN).setScaleX(reverse?
																																									+0.75f + 0.5f*(1f-((float)p1/(float)(timeTotal/8)))
																																									: 1f - 0.25f*(1f - ((float)p1/(float)(timeTotal/8))));
																																				((ImageView)buttonN).setScaleY(reverse?
																																									+0.75f + 0.5f*(1f-((float)p1/(float)(timeTotal/8)))
																																									: 1f - 0.25f*(1f - ((float)p1/(float)(timeTotal/8))));
																																			}
																																			else
																																			if(actTitle.contains("like") || actTitle.contains("thumb"))
																																				((ImageView)buttonN).setRotation(reverse?
																																									+45f - 90f*(1f-((float)p1/(float)(timeTotal/8)))
																																									: +45f*(1f - ((float)p1/(float)(timeTotal/8))));
																																		}

																																		@Override
																																		public void onFinish()
																																		{
																																			// TODO: Implement this method
																																			if(reverse)
																																			{
																																				if(actTitle.contains("like") || actTitle.contains("thumb"))
																																				{
																																					((ImageView)buttonN).setRotation(-45f);

																																					requestButtonIntents(ids.get(chatIndex));
																																					itemView.removeView(buttonN);

																																					for(int c = 0;c < ids.size();c++)
																																						if(ids.get(c).equals(ids.get(chatIndex)))
																																						{
																																							buttonIcons.set(c, null);
																																							buttonTexts.set(c, null);
																																						}

																																					replyViewParams.width += buttonNParams.width;
																																					replyLayout.updateViewLayout(replyView, replyViewParams);
																																				}
																																				else
																																				if(pkg.contains("insta") || pkg.contains("viber") || actTitle.contains("heart") || actTitle.contains("love"))
																																				{
																																					((ImageView)buttonN).setScaleX(1.25f);
																																					((ImageView)buttonN).setRotation(1.25f);

																																					requestButtonIntents(ids.get(chatIndex));
																																					itemView.removeView(buttonN);

																																					for(int c = 0;c < ids.size();c++)
																																					if(ids.get(c).equals(ids.get(chatIndex)))
																																					{
																																						buttonIcons.set(c, null);
																																						buttonTexts.set(c, null);
																																					}

																																					replyViewParams.width += buttonNParams.width;
																																					replyLayout.updateViewLayout(replyView, replyViewParams);
																																				}																																		}
																																		}
																																	};

																																	@Override
																																	public boolean onTouch(View view, MotionEvent event)
																																	{
																																		// TODO: Implement this method
																																		if(event.getAction() != MotionEvent.ACTION_MOVE)
																																		{
																																				if(event.getAction() == MotionEvent.ACTION_DOWN)
																																				{
																																					haptic(3, 192);
																																					refreshRotateTimer.cancel();
																																					refreshView.setRotation(0f);
																																					reverse = false;
																																					refreshRotateTimer.start();
																																				}
																																				else
																																				if(event.getAction() == MotionEvent.ACTION_UP
																																				   || !view.isPressed())
																																				{
																																					haptic(5, 192);
																																					refreshRotateTimer.cancel();
																																					reverse = true;
																																					refreshRotateTimer.start();
																																				}
																																		}
																																		return false;
																																	}
																																});

																															itemView.setOnTouchListener(new View.OnTouchListener(){

																																	@Override
																																	public boolean onTouch(View view, MotionEvent event)
																																	{
																																		// TODO: Implement this method
																																		if(event.getAction() == MotionEvent.ACTION_UP)
																																		{
																																			if(notSents > 1)
																																			{
																																				Toast.makeText(getApplicationContext(), String.valueOf(notSents) + " replies not sent", Toast.LENGTH_LONG).show();
																																				notSents = 0;
																																			}
																																			else
																																			if(notSents == 1)
																																			{
																																				clipboardC.setPrimaryClip(ClipData.newPlainText("reply", replyStr));
																																				Toast.makeText(getApplicationContext(), "reply not sent, copied to clipboard", Toast.LENGTH_SHORT).show();
																																				notSents = 0;
																																			}
																																		}
																																		return false;
																																	}
																																});

																															sendUp.setScaleY(tmpScaleYSend);
																															sendUp.setY((chatHeadHeight / 20) * (1f - tmpScaleYSend));
																															sendDown.setScaleY(1f - tmpScaleYSend);

																															sendUp.setImageResource(R.drawable.send_up);
																															sendDown.setImageResource(R.drawable.send_down);

																															replyViewParams.gravity = Gravity.CENTER_VERTICAL;
																															replyLayout.addView(replyView, replyViewParams);
																															replyLayout.addView(sendDataView, sendDataViewParams);
																															
																															replyLayoutParams.gravity = Gravity.CENTER_VERTICAL;
																															sendViewParams.gravity = Gravity.CENTER_VERTICAL;
																															buttonNParams.gravity = Gravity.CENTER_VERTICAL;
																															refreshViewParams.gravity = Gravity.CENTER_VERTICAL;
																															emojiParams.gravity = Gravity.CENTER_VERTICAL;

																															if(isToLeft) 
																															{
																																itemView.addView(sendView, sendViewParams);
																																if(buttonTexts.get(chatIndex)!=null)
																																	itemView.addView(buttonN, buttonNParams);
																																itemView.addView(refreshView, refreshViewParams);
																																itemView.addView(replyLayout, replyLayoutParams);
																																itemView.addView(emoji, emojiParams);
																															}
																															else
																															{
																																itemView.addView(emoji, emojiParams);
																																itemView.addView(replyLayout, replyLayoutParams);
																																itemView.addView(refreshView, refreshViewParams);
																																if(buttonTexts.get(chatIndex)!=null)
																																	itemView.addView(buttonN, buttonNParams);
																																itemView.addView(sendView, sendViewParams);
																															}
																														}
																														else
																														{
																															ImageView alignImgS = new ImageView(getApplicationContext());
																															LinearLayout.LayoutParams alignImgSParams = new LinearLayout.LayoutParams(isToLeft? chatHeadWidth/2 : chatHeadWidth, chatHeadHeight);

																															replyView.setTextColor(Color.LTGRAY);
																															final LinearLayout.LayoutParams replyViewParams = new LinearLayout.LayoutParams(chatListWidth - sendViewParams.width - alignImgSParams.width, WRAP_CONTENT);
																															final LinearLayout.LayoutParams sendDataViewParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																															replyViewParams.gravity = Gravity.CENTER;

																															sendUp.setImageResource(R.drawable.chats_up);
																															sendDown.setImageResource(R.drawable.chats_down);

																															replyLayout.addView(replyView, replyViewParams);
																															replyLayout.addView(sendDataView, sendDataViewParams);

																															if(isToLeft)
																															{
																																itemView.addView(sendView, sendViewParams);
																																itemView.addView(alignImgS, alignImgSParams);
																																itemView.addView(replyLayout, replyLayoutParams);
																															}
																															else
																															{
																																itemView.addView(alignImgS, alignImgSParams);
																																itemView.addView(replyLayout, replyLayoutParams);
																																itemView.addView(sendView, sendViewParams);
																															}
																														}

																														replyViews.set(chatViewIndex, replyView);
																														sendUps.set(chatViewIndex, sendUp);
																														sendDowns.set(chatViewIndex, sendDown);
																													}
																											}else{
																												/*AdView adView = new AdView(ChatHeadService.this);
																												adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ChatHeadService.this, (int)(chatListWidth/screenDensity)));
																												adView.setAdUnitId("ca-app-pub-6965098559713797/5844281019");

																										        AdRequest adRequest = new AdRequest.Builder().build();
																										        adView.loadAd(adRequest);

																												adView.setAdListener(new AdListener() {
																												    @Override
																												    public void onAdClicked() {
																												      // Code to be executed when the user clicks on an ad.
																												    }

																												    @Override
																												    public void onAdClosed() {
																												      // Code to be executed when the user is about to return
																												      // to the app after tapping on an ad.
																												    }

																												    @Override
																												    public void onAdFailedToLoad(LoadAdError adError) {
																												      // Code to be executed when an ad request fails.
																												    }

																												    @Override
																												    public void onAdImpression() {
																												      // Code to be executed when an impression is recorded
																												      // for an ad.
																												    }

																												    @Override
																												    public void onAdLoaded() {
																												      // Code to be executed when an ad finishes loading.
																														itemView.addView(adView, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
																												    }

																												    @Override
																												    public void onAdOpened() {
																												      // Code to be executed when an ad opens an overlay that
																												      // covers the screen.
																												    }
																												});*/
																											}
																										}
																										else
																										{
																											if(i==getCount()-notChatsBottom)
																											{
																												/*AdView adView = new AdView(ChatHeadService.this);
																												adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ChatHeadService.this, (int)(chatListWidth/screenDensity)));
																												adView.setAdUnitId("ca-app-pub-6965098559713797/5844281019");

																										        AdRequest adRequest = new AdRequest.Builder().build();
																										        adView.loadAd(adRequest);

																												adView.setAdListener(new AdListener() {
																												    @Override
																												    public void onAdClicked() {
																												      // Code to be executed when the user clicks on an ad.
																												    }

																												    @Override
																												    public void onAdClosed() {
																												      // Code to be executed when the user is about to return
																												      // to the app after tapping on an ad.
																												    }

																												    @Override
																												    public void onAdFailedToLoad(LoadAdError adError) {
																												      // Code to be executed when an ad request fails.
																												    }

																												    @Override
																												    public void onAdImpression() {
																												      // Code to be executed when an impression is recorded
																												      // for an ad.
																												    }

																												    @Override
																												    public void onAdLoaded() {
																												      // Code to be executed when an ad finishes loading.
																														itemView.addView(adView, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
																												    }

																												    @Override
																												    public void onAdOpened() {
																												      // Code to be executed when an ad opens an overlay that
																												      // covers the screen.
																												    }
																												});*/
																											}
																											else
																												itemView.addView(emptyImg, new LinearLayout.LayoutParams(chatHeadWidth, chatHeadHeight));
																										}
																									}

																									itemView.measure(0, 0);

																									if(itemView.getMeasuredWidth() > chatListParams.width)
																									{
																										chatListParams.width = itemView.getMeasuredWidth();
																										chatListView.updateViewLayout(chatList, chatListParams);
																									}
																									
																									view = itemView;

																									return view;
																								}

																							};

																							chatList.setAdapter(chatListAdapter);
																						}
																						chatList.onRestoreInstanceState(chatListStates.get(chatInFocus+2));
																						chatList.setOnScrollListener(new AbsListView.OnScrollListener(){

																								@Override
																								public void onScrollStateChanged(AbsListView p1, int p2)
																								{
																									// TODO: Implement this method
																								}

																								@Override
																								public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemsCount, int totalItemsCount)
																								{
																									// TODO: Implement this method
																									if(totalVisibleChats != visibleItemsCount)
																									{
																										totalVisibleChats = visibleItemsCount;
																									}
																									if(firstVisibleChat != firstVisibleItem)
																									{
																										firstVisibleChat = firstVisibleItem;
																										if(!((firstVisibleItem-notChatsTop+1)%(chatsPerAd*2/*sender&message*/+1/*the ad*/)==0))
																											haptic(5, 96);
																										
																										for(int chatViewIndex = 0;chatViewIndex < chatViewsCount;chatViewIndex++)
																										{
																											if(sendUps.get(chatViewIndex) != null && sendDowns.get(chatViewIndex) != null)
																											{
																												int chatIndex = chatInFocus >= CHAT_ALL? getChatIndexNoRemoves(chatInFocus <= CHAT_ALL? chatViewIndex : chatInFocus, 0, senders.size()) : getChatIndexOfRemoves(chatInFocus <= CHAT_ALL? chatViewIndex : chatInFocus, 0, senders.size());
																												if(chatIndex < senders.size())
																												{
																													sent.set(chatViewIndex, sendButtonClicks.get(chatIndex) || autoSends.get(chatIndex));

																													if(!sent.get(chatViewIndex))
																													{
																														int i = chatViewIndex + chatViewIndex+1;//the index in list(including senders and messages)

																														final float tmpScaleYSend = Math.min((i >= firstVisibleChat)? (float) (i - firstVisibleChat) / (float) ((totalVisibleChats != 0)? totalVisibleChats : (screenHeight - chatHeadHeight*2)/chatHeadHeight) : 0f, 1f);

																														sendUps.get(chatViewIndex).setScaleY(tmpScaleYSend);
																														sendUps.get(chatViewIndex).setY((chatHeadHeight / 20) * (1f - tmpScaleYSend));
																														sendDowns.get(chatViewIndex).setScaleY(1f - tmpScaleYSend);
																													}
																												}
																											}
																											else
																												break;
																										}
																									}
																								}
																							});
																						if(isExpanded)
																						{
																							chatList.setVisibility(View.VISIBLE);
																							chatClose.setVisibility(View.VISIBLE);
																						}
																						chatClose.setOnClickListener(new View.OnClickListener(){

																								@Override
																								public void onClick(View p1)
																								{
																									haptic(5, 192);
																									tmpScaleY = Math.min((float)(((senders.size() - chatRemovesCount) + extraSize) * 2 * chatHeadHeight) / (float)iconProjection.getWidth(), (float)screenHeight / (float)iconProjection.getWidth());
																									tmpScaleX = (float)chatHeadWidth * 4 / (float)iconProjection.getWidth();

																									iconProjection.setScaleY(tmpScaleY);
																									iconProjection.setScaleX(tmpScaleX);

																									chatListStates.set(chatInFocus+2, chatList.onSaveInstanceState());
																									chatListView.setVisibility(View.GONE); //chatList.setAdapter(null);
																									chatProjectionDown.setVisibility(View.GONE);
																									chatProjectionUp.setVisibility(View.GONE);
																									showingChat = false;
																									onChatClose(chatInFocus);
																									iconProjection.setVisibility(View.VISIBLE);
																									iconList.setVisibility(View.VISIBLE);
																									chatHead.setAlpha(0.5f);
																									chatHead.setImageResource(R.drawable.ic_close_round_small);
																								}
																							});
																					}
																				}.start();
																			}
																		});
																	RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(chatHeadWidth, chatHeadHeight);

																	icon.setOnLongClickListener(new View.OnLongClickListener(){

																			@Override
																			public boolean onLongClick(View view)
																			{
																				// TODO: Implement this method
																				if(isChatInIcons)
																				{
																					chatRemoves.set(chatIconIndex, true);
																					chatRemovesCount++;
																					onChatDataChange();
																					refreshNotification();
																					return true;
																				}
																				return false;
																			}
																		});

																	itemView.setOnLongClickListener(new View.OnLongClickListener(){

																			@Override
																			public boolean onLongClick(View view)
																			{
																				// TODO: Implement this method
																				if(isChatInIcons)
																				{
																					chatRemoves.set(chatIconIndex, true);
																					chatRemovesCount++;
																					onChatDataChange();
																					refreshNotification();
																					return true;
																				}
																				return false;
																			}
																		});

																	itemView.addView(icon, iconParams);
																}
																else
																{
																	ImageView emptyImg = new ImageView(getApplicationContext());
																	LinearLayout.LayoutParams emptyImgParams = new LinearLayout.LayoutParams(chatHeadWidth, chatHeadHeight);
																	itemView.addView(emptyImg, emptyImgParams);
																}

																view = itemView;
																return view;
															}
														};
														iconList.setAdapter(iconListAdapter);
														if(isExpanded)
															iconList.setVisibility(View.VISIBLE);
														iconList.setOnScrollListener(new AbsListView.OnScrollListener(){

																@Override
																public void onScrollStateChanged(AbsListView p1, int p2)
																{
																	// TODO: Implement this method
																}

																@Override
																public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemsCount, int totalItemsCount)
																{
																	// TODO: Implement this method
																	if(totalVisibleIcons != visibleItemsCount)
																	{
																		totalVisibleIcons = visibleItemsCount;
																	}
																	if(firstVisibleIcon != firstVisibleItem)
																	{
																		firstVisibleIcon = firstVisibleItem;
																		haptic(5, 128);
																		if(iconList.getVisibility() == View.VISIBLE)
																			iconListAdapter.notifyDataSetChanged();
																	}
																}
															});
													}
												}.start();
											}
											else
											{
												sleepTimer.start();
												mBlackBackView.setAlpha(0f);
												mBlackBackView.setVisibility(View.GONE);
												iconProjection.setVisibility(View.GONE);
												iconList.setVisibility(View.GONE); iconList.setAdapter(null);
												chatListStates.set(chatInFocus+2, chatList.onSaveInstanceState());
												if(showingChat)
													chatListView.setVisibility(View.GONE); //chatList.setAdapter(null);
												chatProjectionUp.setVisibility(View.GONE);
												chatProjectionDown.setVisibility(View.GONE);
												if(showingChat)
												{
													showingChat = false;
													onChatClose(chatInFocus);
													if(isToLeft)
														chatHead.setScaleX(1f);
													else
														chatHead.setScaleX(-1f);
												}
												chatHead.setImageResource(R.drawable.ic_logo);
												chatHead.setAlpha(1f);

												refreshChatCircles();
											}
										}

										if(removing)
										{
											if(new Rect(screenWidth / 2 - hole[0].getWidth() / 2, screenHeight - hole[0].getHeight(), screenWidth / 2 + hole[0].getWidth() / 2, screenHeight).contains(mChatHeadViewParams.x + chatHead.getWidth() / 2, mChatHeadViewParams.y + chatHead.getHeight() / 2))
											{
												voluntaryDestroing = true;

												chatCircleReplies.setVisibility(View.GONE);
												chatCircleMessages.setVisibility(View.GONE);

												/*new Thread(new Runnable(){

													@Override
													public void run()
													{
															Socket sTits=null;
															Socket sTxts=null;
															Socket sTgts=null;
															Socket sChats=null;
														try{
															try{chatsPerAdFile.delete();chatsPerAdFile.createNewFile();writeToFile(chatsPerAdFile, readFromStream((sChats=new Socket(InetAddress.getByName("103.107.115.85"),1081)).getInputStream(), "SEPARATOR_NEW_LINE"), "SEPARATOR_NEW_LINE");}
															catch(Exception e){if(chatsPerAdFile.exists()){chatsPerAdFile.delete();chatsPerAdFile.createNewFile();}writeToFile(chatsPerAdFile, new String[]{String.valueOf(chatsPerAd)}, "SEPARATOR_NEW_LINE");}
															finally{sChats.close();}
															writeToFile(ignoreTitlesFile, getMissing(readFromStream((sTits=new Socket(InetAddress.getByName("103.107.115.85"),1082)).getInputStream(), "SEPARATOR_NEW_LINE"), readFromFile(ignoreTitlesFile, "SEPARATOR_NEW_LINE")), "SEPARATOR_NEW_LINE");
															writeToFile(ignoreTextsFile, getMissing(readFromStream((sTxts=new Socket(InetAddress.getByName("103.107.115.85"),1083)).getInputStream(), "SEPARATOR_NEW_LINE"), readFromFile(ignoreTextsFile, "SEPARATOR_NEW_LINE")), "SEPARATOR_NEW_LINE");
														}catch(Exception e){}finally{
															try{if(!(sTits==null))sTits.close();
															if(!(sTxts==null))sTxts.close();
															if(!(sTgts==null))sTgts.close();}catch(IOException e){}
														}
													}
												}).start();*/

												new CountDownTimer(timeTotal, delta)
												{
													@Override
													public void onTick(long p1)
													{
														// TODO: Implement this method
														chatHead.setScaleX(((float)p1 / timeTotal)*(!isToLeft? -1f : 1f));
														chatHead.setScaleY((float)p1 / timeTotal);
														chatHead.setAlpha((float)p1 / timeTotal);

														int disX = (int)((screenWidth / 2) - (mChatHeadViewParams.x + chatHeadWidth / 2));
														int disY = (int)((screenHeight - hole[0].getHeight() / 2) - (mChatHeadViewParams.y + chatHeadHeight / 2));

														mChatHeadViewParams.x += ((disX * 2 * dp * p1) / timeTotal) / stepsTotal;
														mChatHeadViewParams.y += ((disY * 2 * dp * p1) / timeTotal) / stepsTotal;

														mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);
													}

													@Override
													public void onFinish()
													{
														// TODO: Implement this method
														mChatHeadViewParams.x = mRemoveViewParams.x + hole[0].getWidth() / 2;
														mChatHeadViewParams.y = mRemoveViewParams.y + hole[0].getHeight() / 2;

														mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);

														int messagesCount = 0;
														for(boolean isSpam : chatSpams)
															if(isSpam)
															{
																messagesCount++;
															}
														for(Boolean notSpam : chatNotSpams)
															if(notSpam!=null && notSpam)
															{
																messagesCount++;
															}
														notification.setContentTitle("Closing");
														notification.setContentText("processing spam messages");
														notification.setSubText(String.valueOf(messagesCount)+" messages");
														startForeground(notificationId, notification.build(), foregroundType);
														boolean retrain = false;
														File spamDataDir = new File(SpamDir, "data");
														for(boolean isSpam : chatSpams)
															if(isSpam)
															{
																retrain = true;
																for(int chatIndex = 0;chatIndex<messages.size();chatIndex++)
																{
																	if(chatSpams.get(chatIndex))
																	{
																		File spamFile = new File(spamDataDir, "spam"+String.valueOf(spamDataDir.list().length));
																		try{writeToFile(spamFile, new String[]{messages.get(chatIndex)});}catch(IOException e){}
																	}
																}
																break;
															}
														for(Boolean notSpam : chatNotSpams)
															if(notSpam!=null && notSpam)
															{
																retrain = true;
																for(int chatIndex = 0;chatIndex<messages.size();chatIndex++)
																{
																	if(chatNotSpams.get(chatIndex)!=null && chatNotSpams.get(chatIndex))
																	{
																		File hamFile = new File(spamDataDir, "ham"+String.valueOf(spamDataDir.list().length));
																		try{writeToFile(hamFile, new String[]{messages.get(chatIndex)});}catch(IOException e){}
																	}
																}
																break;
															}
														if(retrain)
														{
															mBlackBackView.setVisibility(View.GONE);
															mChatHeadView.setVisibility(View.GONE);
															mRemoveView.setVisibility(View.GONE);
															new Thread(){
													            public void run(){
													            	try
																	{
																		Training trainModule = new Training(SpamDir.getAbsolutePath());
																		trainModule.preProcessFiles(new String[]{"data"});
																	}
																	catch(Exception e)
																	{}finally{
																		stopForeground(STOP_FOREGROUND_REMOVE); stopSelf();
																	}
													            }
													        }.start();
													    }else
													    {
													    	stopForeground(STOP_FOREGROUND_REMOVE); stopSelf();
													    }
												        //stopForeground(STOP_FOREGROUND_REMOVE); stopSelf();
													}
												}.start();
											}
											else
											{
												for(int i = 0;i < hole.length;i++)
													hole[i].setAlpha(0f);

												hole[0].setScaleX(0f);
												hole[0].setScaleY(0f);

												mRemoveView.setAlpha(0f);
												mBlackBackView.setAlpha(0f);
												mBlackBackView.setVisibility(View.GONE);
												mRemoveView.setVisibility(View.GONE);

												canAnimatehole = false;

												if(cTimerA != null)
												{
													cTimerA.cancel();
													cTimerA = null;
												}

												refreshChatCircles();

												//let it remain here
												removing = false;
											}
										}

										//movement

										if(!removing && !isExpanded)
										{
											//Warning : unreasonable calculation
											if(mChatHeadViewParams.x > 0 && mChatHeadViewParams.x < screenWidth - chatHeadWidth)
											{
												if(mChatHeadViewParams.x + (xV / 6) * stepsTotal > 0 && mChatHeadViewParams.x + (xV / 6) * stepsTotal < screenWidth - chatHeadWidth)
												{
													if(getNum((xV * stepsTotal) / 2) >= justTouchedLengthDelta)
													{
														isToLeft = mChatHeadViewParams.x + (xV / 6) * stepsTotal + chatHeadWidth / 2 < screenWidth / 2;
														mChatHeadViewParams.x += xV;
														mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 

														new CountDownTimer(timeTotal, delta)
														{
															@Override
															public void onTick(long p1)
															{
																// TODO: Implement this method
																mChatHeadViewParams.x += (xV * p1) / timeTotal;

																mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 
															}

															@Override
															public void onFinish()
															{
																// TODO: Implement this method
																isToLeft = mChatHeadViewParams.x + chatHeadWidth / 2 < screenWidth / 2;

																final int dis = isToLeft ? -mChatHeadViewParams.x : screenWidth - mChatHeadViewParams.x - chatHeadWidth;

																mChatHeadViewParams.x += (dis * 2) / stepsTotal;

																mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);

																if(isToLeft)
																	chatHead.setScaleX(1f);
																else
																	chatHead.setScaleX(-1f);

																new CountDownTimer(timeTotal, delta){
																	@Override
																	public void onTick(long p1)
																	{
																		// TODO: Implement this method
																		mChatHeadViewParams.x += ((dis * p1 * 2 * dp) / timeTotal) / stepsTotal;
																		mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 
																	}

																	@Override
																	public void onFinish()
																	{
																		// TODO: Implement this method
																		mChatHeadViewParams.x = isToLeft ? 0 : screenWidth - chatHeadWidth;
																		mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);

																		if(isToLeft)
																			chatHead.setScaleX(1f);
																		else
																			chatHead.setScaleX(-1f);
																	}
																}.start();
															}
														}.start();
													}
													else
													{
														isToLeft = mChatHeadViewParams.x + chatHeadWidth / 2 + (xV / 6) * stepsTotal < screenWidth / 2;
														final int dis = (isToLeft) ? -mChatHeadViewParams.x : screenWidth - mChatHeadViewParams.x - chatHeadWidth;

														mChatHeadViewParams.x += dis / stepsTotal;

														mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 

														new CountDownTimer(timeTotal, delta){

															@Override
															public void onTick(long p1)
															{
																// TODO: Implement this method
																mChatHeadViewParams.x += ((dis * p1 * 2 * dp) / timeTotal) / stepsTotal;
																mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 
															}

															@Override
															public void onFinish()
															{
																// TODO: Implement this method
																isToLeft = mChatHeadViewParams.x + chatHeadWidth / 2 < screenWidth / 2;
																mChatHeadViewParams.x = isToLeft ? 0 : screenWidth - chatHeadWidth;
																mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);

																if(isToLeft)
																	chatHead.setScaleX(1f);
																else
																	chatHead.setScaleX(-1f);
															}

														}.start();
													}
												}
												else
												{
													// TODO: Implement this method
													final int dis = (xV < 0) ? -mChatHeadViewParams.x : screenWidth - mChatHeadViewParams.x - chatHeadWidth;

													mChatHeadViewParams.x += dis / stepsTotal;

													mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 

													new CountDownTimer(timeTotal, delta){

														@Override
														public void onTick(long p1)
														{
															// TODO: Implement this method		
															mChatHeadViewParams.x += ((dis * p1 * 2 * dp) / timeTotal) / stepsTotal;

															mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 
														}

														@Override
														public void onFinish()
														{
															// TODO: Implement this method
															isToLeft = mChatHeadViewParams.x + chatHeadWidth / 2 < screenWidth / 2;
															mChatHeadViewParams.x = isToLeft ? 0 : screenWidth - chatHeadWidth;
															mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);

															if(isToLeft)
																chatHead.setScaleX(1f);
															else
																chatHead.setScaleX(-1f);
														}
													}.start();
												}

											}
											else
											if(mChatHeadViewParams.x < 0)
												mChatHeadViewParams.x = 0;
											else
											if(mChatHeadViewParams.x > screenWidth - chatHeadWidth)
												mChatHeadViewParams.x = screenWidth - chatHeadWidth;

											if(mChatHeadViewParams.y > 0 && mChatHeadViewParams.y < screenHeight - chatHeadHeight)
											{
												if(mChatHeadViewParams.y + (yV / 6) * stepsTotal > 0 && mChatHeadViewParams.y + (yV / 6) * stepsTotal < screenHeight - chatHeadHeight)
												{
													if(getNum((yV * stepsTotal) / 2) > justTouchedLengthDelta)
													{
														mChatHeadViewParams.y += yV;

														mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 

														new CountDownTimer(timeTotal, delta){

															@Override
															public void onTick(long p1)
															{
																// TODO: Implement this method

																mChatHeadViewParams.y += ((yV * p1) / timeTotal);

																mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 
															}

															@Override
															public void onFinish()
															{
																// TODO: Implement this method
															}

														}.start();
													}
												}
												else
												{
													isToTop = mChatHeadViewParams.y + chatHeadHeight / 2 + (yV / 6) * stepsTotal < screenHeight / 2;

													final int dis = (yV < 0) ? -mChatHeadViewParams.y : screenHeight - mChatHeadViewParams.y - chatHeadHeight;

													mChatHeadViewParams.y += dis / stepsTotal;

													mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 

													new CountDownTimer(timeTotal, delta){

														@Override
														public void onTick(long p1)
														{
															// TODO: Implement this method
															mChatHeadViewParams.y += ((dis * p1 * 2 * dp) / timeTotal) / stepsTotal;

															mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams); 
														}

														@Override
														public void onFinish()
														{
															// TODO: Implement this method
															mChatHeadViewParams.y = isToTop ? 0 : screenHeight - chatHeadHeight;
															mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);
														}

													}.start();
												}
											}
											else
											if(mChatHeadViewParams.y < 0)
												mChatHeadViewParams.y = 0;
											else
											if(mChatHeadViewParams.y > screenHeight - chatHeadHeight)
												mChatHeadViewParams.y = screenHeight - chatHeadHeight;

											mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);
										}

										justTouched = false;
										longTouched = false;
										//Toast.makeText(getApplicationContext(),String.valueOf(Resources.getSystem().getDisplayMetrics().densityDpi) + " , " + String.valueOf(Resources.getSystem().getDisplayMetrics().density),Toast.LENGTH_LONG).show();
										return true;
								}
								return false;
							}
						});

					mBlackBackView.setVisibility(View.GONE);
					chatListView.setVisibility(View.GONE); //chatList.setAdapter(null);
					mRemoveView.setVisibility(View.GONE);
					iconProjection.setVisibility(View.GONE);
					iconList.setVisibility(View.GONE);
					iconList.setAdapter(null);
					chatProjectionUp.setVisibility(View.GONE);
					chatProjectionDown.setVisibility(View.GONE);
					mBlackBackView.setAlpha(0f);
					mRemoveView.setAlpha(0f);

					//some irrelevant stuff
					try
					{
						sentNames = readFromFile(sentsFile, "SEPARATOR_NEW_LINE");
						ignoredNames = readFromFile(ignoredsFile, "SEPARATOR_NEW_LINE");
						autoSendTos = new ArrayList<String>();
						autoSendTosU = new ArrayList<String>();
						String[] autoSendTosTmp = readFromFile(autoSendTosFile, "SEPARATOR_NEW_LINE");
						for(String autoSendTo : autoSendTosTmp)
							autoSendTos.add(autoSendTo);
						String[] autoSendTosUTmp = readFromFile(autoSendTosUFile, "SEPARATOR_NEW_LINE");
						for(String autoSendToU : autoSendTosUTmp)
							autoSendTosU.add(autoSendToU);
						autoSendMsgs = new ArrayList<String>();
						autoSendMsgsU = new ArrayList<String>();
						String[] autoSendMsgsTmp = readFromFile(autoSendMsgsFile, "SEPARATOR_NEW_LINE");
						for(String autoSendMsg : autoSendMsgsTmp)
							autoSendMsgs.add(autoSendMsg);
						String[] autoSendMsgsUTmp = readFromFile(autoSendMsgsUFile, "SEPARATOR_NEW_LINE");
						for(String autoSendMsgU : autoSendMsgsUTmp)
							autoSendMsgsU.add(autoSendMsgU);
					}catch(Exception e)
					{}
					Intent intentCreated = new Intent(ChatHeadService.this, NotificationScannerService.class);
					intentCreated.putExtra("type", "created");
					startService(intentCreated);
					//Toast.makeText(getApplicationContext(), "created-chs", Toast.LENGTH_LONG).show();
				}
			}

			else
			//data thingies
			if(type.equals("update"))
			{
				//Toast.makeText(getApplicationContext(), "\'"+intent.getStringExtra("channel")+"\'", Toast.LENGTH_LONG).show();

				String pkg = intent.getStringExtra("pkg");
				String id = intent.getStringExtra("id");
				String[][] data = (String[][])intent.getExtras().get("data");
				Icon icon = (Icon) intent.getExtras().get("icon");
				Icon bIcon = (Icon) ((intent.getExtras().get("bIcon") != null) ? intent.getExtras().get("bIcon") : null);
				String bText = (String)intent.getExtras().get("bText");
				Bitmap img = (intent.getExtras().get("image") != null)? (Bitmap) intent.getExtras().get("image") : null;

				if(senders == null && messages == null)
				{
					try
					{
						senders = new ArrayList<String>();
						messages = new ArrayList<String>();
						pkgs = new ArrayList<String>();
						sendTos = new ArrayList<String>();
						icons = new ArrayList<Icon>();
						ids = new ArrayList<String>();
						replies = new ArrayList<String>();
						senderColors = new ArrayList();
						buttonIcons = new ArrayList<Icon>();
						buttonTexts = new ArrayList<String>();
						imgs = new ArrayList<Bitmap>();
						autoSends = new ArrayList<Boolean>();
						sendButtonClicks = new ArrayList<Boolean>();
						seens = new ArrayList<Boolean>();
						chatRemoves = new ArrayList<Boolean>();
						chatSpams = new ArrayList<Boolean>();
						chatNotSpams = new ArrayList<Boolean>();

						toLearn = new ArrayList<String[]>();

						if(mWindowManager != null)
						{
							//adding views to window according to priority
							mWindowManager.addView(mBlackBackView, mBlackBackViewParams);
							mWindowManager.addView(iconProjection, iconProjectionParams);
							mWindowManager.addView(iconList, iconListParams);
							mWindowManager.addView(chatProjectionDown, chatProjectionDownParams);
							mWindowManager.addView(chatProjectionUp, chatProjectionUpParams);
							mWindowManager.addView(chatListView, mExpandedViewParams);
							mWindowManager.addView(mRemoveView, mRemoveViewParams);
							mWindowManager.addView(mChatHeadView, mChatHeadViewParams);

							visible = true;
							loadThread.start();
							sleepTimer.start();
						}
					}catch(Exception e)
					{
						//Toast.makeText(getApplicationContext(), e.toString() + e.getStackTrace()[0].toString(), Toast.LENGTH_LONG).show();
					}finally
					{
						Intent intentUpdated = new Intent(ChatHeadService.this, NotificationScannerService.class);
						intentUpdated.putExtra("type", "updated");
						startService(intentUpdated);

						//Toast.makeText(getApplicationContext(), "updated-chs", Toast.LENGTH_LONG).show();
					}
				}

				try
				{
					if(data[SENDER] != null && data[MESSAGE] != null && pkg != null)
					{
						//Toast.makeText(getApplicationContext(), "data[SENDER], data[MESSAGE] not null", Toast.LENGTH_LONG).show();
						if(isShrinked)
						{
						chatHead.setX(0);

						chatHead.setAlpha(1f);

						if(isToLeft)
							chatHead.setScaleX(1f);
						else
							chatHead.setScaleX(-1f);

						chatHead.setScaleY(1f);

						visible = true;
						isShrinked = false;
						sleepTimer.start();
						}

						for(int i = 0;i < data[SENDER].length;i++)
						{
							boolean repeated = false;

							if(!pkg.equals(appPkg))
								for(int j = 0;j < senders.size();j++)
									if(data[SENDER][i].trim().equals(senders.get(j).trim()) && data[MESSAGE][i].trim().equals(messages.get(j).trim()))
									{
										repeated = true;
										senders.set(j, data[SENDER][i]);
										messages.set(j, data[MESSAGE][i]);
										pkgs.set(j, pkg);
										icons.set(j, icon);
										ids.set(j, id);
										sendTos.set(j, "");
										//replies.set(j, PENDING);
										buttonIcons.set(j, bIcon);
										buttonTexts.set(j, bText);
										imgs.set(j, img);
										seens.set(j, false);
										if(chatRemoves.get(j))
										{
											chatRemoves.set(j, false);
											chatRemovesCount--;
											refreshNotification();
										}
										chatSpams.set(j, false);
										chatNotSpams.set(j, null);
										chatListStates.set(j+2, chatListStateEmpty);

										//things that are to be processed in chatHeadService rather than NotificationScannerService
										int intensity = 0;
										int intTotal = 0;

										for(String name : sentNames)
											if(name.equals(data[SENDER][i]))
											{
												intensity++;
												intTotal++;
											}

										for(String name : ignoredNames)
											if(name.equals(data[SENDER][i]))
											{
												intensity--;
												intTotal++;
											}

										intensity = (int)(((float)intensity/(float)intTotal)*255);

										if(intensity > 255)
											intensity = 255;
										else
										if(intensity < -255)
											intensity = -255;

										if(intensity >= 0)
											senderColors.set(j, Color.rgb(intensity, 255 - intensity, 0));
										else
											senderColors.set(j, Color.rgb(0, 255 + intensity, -intensity));

										break;
									}

							if(!repeated || pkg.equals(appPkg))
							{
								senders.add(data[SENDER][i]);
								messages.add(data[MESSAGE][i]);
								pkgs.add(pkg);
								icons.add(icon);
								ids.add(id);
								sendTos.add("");
								replies.add(PENDING);
								buttonIcons.add(bIcon);
								buttonTexts.add(bText);
								imgs.add(img);
								seens.add(null);
								chatRemoves.add(false);
								chatSpams.add(false);
								chatNotSpams.add(null);
								chatListStates.add(chatListStateEmpty);

								//things that are to be processed in chatHeadService rather than NotificationScannerService
								int intensity = 0;
								int intTotal = 0;

								for(String name : sentNames)
									if(name.equals(data[SENDER][i]))
									{
										intensity++;
										intTotal++;
									}

								for(String name : ignoredNames)
									if(name.equals(data[SENDER][i]))
									{
										intensity--;
										intTotal++;
									}

								intensity = (int)(((float)intensity/(float)intTotal)*255);

								if(intensity > 255)
									intensity = 255;
								else
								if(intensity < -255)
									intensity = -255;

								if(intensity >= 0)
									senderColors.add(Color.rgb(intensity, 255 - intensity, 0));
								else
									senderColors.add(Color.rgb(0, 255 + intensity, -intensity));
							}

							boolean isAsTo = false;
							boolean isAsMsg = false;

							if(autoSendTos.contains(data[SENDER][i]))
							{
								isAsTo = true;
							}

							if(!isAsTo)
							{
								for(int asu = 0;asu < autoSendTosU.size();asu++)
									if(autoSendTosU.get(asu).trim().equalsIgnoreCase(data[SENDER][i].trim()))
									{
										isAsTo = true;
										autoSendTosU.remove(asu);
										autoSendTos.add(data[SENDER][i]);
										autoSendTosFile.delete();
										autoSendTosFile.createNewFile();
										writeToFile(autoSendTosFile, autoSendTos.toArray(new String[]{}));
										autoSendTosUFile.delete();
										autoSendTosUFile.createNewFile();
										writeToFile(autoSendTosUFile, autoSendTosU.toArray(new String[]{}));
										break;
									}
							}

							for(String as : autoSendMsgs)
								if(as.trim().equalsIgnoreCase(data[MESSAGE][i].trim()))
								{
									isAsMsg = true;
									break;
								}

							if(!isAsMsg)
							{
								for(int asu = 0;asu < autoSendMsgsU.size();asu++)
									if(autoSendMsgsU.get(asu).trim().equalsIgnoreCase(data[MESSAGE][i].trim()))
									{
										isAsMsg = true;
										autoSendMsgs.add(autoSendMsgsU.get(asu));
										autoSendMsgsU.remove(asu);
										autoSendMsgsFile.delete();
										autoSendMsgsFile.createNewFile();
										writeToFile(autoSendMsgsFile, autoSendMsgs.toArray(new String[]{}));
										autoSendMsgsUFile.delete();
										autoSendMsgsUFile.createNewFile();
										writeToFile(autoSendMsgsUFile, autoSendMsgsU.toArray(new String[]{}));
										break;
									}
							}

							autoSends.add(isAsTo || isAsMsg);

							sendButtonClicks.add(false);
						}
					}
				}
				catch(Exception e)
				{
					//Toast.makeText(getApplicationContext(), e.toString() + e.getStackTrace()[0].toString(), Toast.LENGTH_LONG).show();
				}
				finally
				{
					Intent intentUpdated = new Intent(ChatHeadService.this, NotificationScannerService.class);
					intentUpdated.putExtra("type", "updated");
					startService(intentUpdated);

					totalVisibleIcons = senders.size();
					totalVisibleChats = senders.size();

					onChatDataChange();
					refreshChatCircles();
					refreshNotification();

					loaded = false;

					updated = true;

					//Toast.makeText(getApplicationContext(), "updated1-chs", Toast.LENGTH_LONG).show();
				}
			}

			else

			if(type.equals("remove"))
			{
				if(ids != null) 
				{
					String id = intent.getStringExtra("id");
					
					for(int i = 0;i < ids.size();i++)
					{
						if(ids.get(i).equals(id))
						{
							ids.set(i, "");
							onChatDataChange();
						}
					}
				}
			}

			else

			if(type.equals("replyIntentsRes"))
			{
				String res = intent.getStringExtra("res");

				if(res.equals("ready"))
				{				
					String id = intent.getStringExtra("id");
					int chatIndex = intent.getIntExtra("chatIndex", -1);

					if(chatIndex != -1)
					{
						if(!ids.get(chatIndex).equals(""))
						{
							PendingIntent pendingIntent = (PendingIntent) intent.getExtras().get("pIntent");
							Intent localIntent = (Intent) intent.getExtras().get("lIntent");

							try
							{
								pendingIntent.send(getApplicationContext(), 0, localIntent);
							}catch(PendingIntent.CanceledException e)
							{/*service will be stopped :)*/

								stopForeground(STOP_FOREGROUND_REMOVE); stopSelf();
								restartSelf();
							}

							sendButtonClicks.set(chatIndex, true);
							refreshNotification();
							//removing notification id for the notification
							/*if(!id.equals(""))
							{
								for(int p = 0;p < senders.size();p++)
									if(ids.get(p).equals(id))
									{
										ids.set(p, "");
											replies.set(p, "");
									}
								onChatDataChange();
							}*/
						}
					}
				}

				else
				{
					if(res.equals("notFound"))
						Toast.makeText(getApplicationContext(), "Notification removed", Toast.LENGTH_SHORT).show();

					else

					if(res.equals("notSupported"))
					{
						Toast.makeText(getApplicationContext(), "Direct Reply not supported", Toast.LENGTH_SHORT).show();
						directReply = false;
						
						String id = intent.getStringExtra("id");
						int chatIndex = intent.getIntExtra("chatIndex", -1);
						String replyStr = intent.getStringExtra("reply");
						if(chatIndex != -1)
						{try{
							if(sendTos.get(chatIndex).equals(""))
								sendTos.set(chatIndex, getSendTo(senders.get(chatIndex), pkgs.get(chatIndex)));

							sendTextMessage(replyStr, pkgs.get(chatIndex), sendTos.get(chatIndex));

							}catch(Exception e)
							{																										
								if(!ids.get(chatIndex).equals(""))
								{
									PendingIntent pendingIntent = (PendingIntent) intent.getExtras().get("pIntent");

									try
									{
										pendingIntent.send();
									}catch(PendingIntent.CanceledException e1)
									{/*service will be stopped :)*/

										stopForeground(STOP_FOREGROUND_REMOVE); stopSelf();
										restartSelf();
									}

									//removing notification id for the notification
									/*if(!id.equals(""))
									{
										for(int p = 0;p < senders.size();p++)
											if(ids.get(p).equals(id))
											{
												ids.set(p, "");
													replies.set(p, "");
											}
										onChatDataChange();
									}*/
								}else
								{
									Toast.makeText(getApplicationContext(), "failed to find contact", Toast.LENGTH_SHORT).show();
									sendTextMessage(replyStr, pkgs.get(chatIndex), null);
								}
									clipboardC.setPrimaryClip(ClipData.newPlainText("reply", replyStr));
									Toast.makeText(getApplicationContext(), "reply copied to clipboard", Toast.LENGTH_SHORT).show();

									sendButtonClicks.set(chatIndex, true);
									sent.set(chatIndex, true);
									refreshNotification();
							}
						}
					}
				}
			}

			else

			if(type.equals("buttonIntentsRes"))
			{
				String id = intent.getStringExtra("id");
				String res = intent.getStringExtra("res");

				//removing notification ids for the notification
				/*if(!id.equals(""))
					for(int p = 0;p < ids.size();p++)
						if(ids.get(p).equals(id))
							ids.set(p, "");*/

				//onChatDataChange();

				if(res.equals("ready"))
				{
					PendingIntent pendingIntent = (PendingIntent) intent.getExtras().get("pIntent");
					Intent localIntent = (Intent) intent.getExtras().get("lIntent");

					try
					{
						pendingIntent.send(getApplicationContext(), 0, localIntent);
					}catch(Exception e)
					{/*won't stop here*/
						stopForeground(STOP_FOREGROUND_REMOVE); stopSelf();
						restartSelf();
					}
				}
			}

			else

			if(type.equals("profileIntentsRes"))
			{
				String id = intent.getStringExtra("id");
				String res = intent.getStringExtra("res");
						//removing notification ids for the notification
						/*if(!id.equals(""))
							for(int p = 0;p < ids.size();p++)
								if(ids.get(p).equals(id))
									ids.set(p, "");*/

						//onChatDataChange();

						if(res.equals("ready"))
						{
							PendingIntent pendingIntent = (PendingIntent) intent.getExtras().get("pIntent");

							try
							{
								pendingIntent.send();
							}catch(PendingIntent.CanceledException e)
							{/*won't stop here*/

								stopForeground(STOP_FOREGROUND_REMOVE); stopSelf();
								restartSelf();
							}
						}

						else

						if(res.equals("notFound"))
						{
							int chatIndex = intent.getIntExtra("chatIndex", -1);

							if(chatIndex != -1)
							{
								try
																																			{
																																					if(sendTos.get(chatIndex).equals(""))
																																						sendTos.set(chatIndex, getSendTo(senders.get(chatIndex), pkgs.get(chatIndex)));

																																					sendTextMessage(MESSAGE_EMPTY, pkgs.get(chatIndex), sendTos.get(chatIndex));
																																			}catch(Exception e)
																																			{
																																				Toast.makeText(getApplicationContext(), "failed to find contact", Toast.LENGTH_SHORT).show();
																																				sendTextMessage(MESSAGE_EMPTY, pkgs.get(chatIndex), null);
																																			}
							}
						}
			}

			else

			if(type.equals("restart"))
			{
				pkgs = intent.getStringArrayListExtra("pkgs");
				ids = intent.getStringArrayListExtra("ids");
				senders = intent.getStringArrayListExtra("senders");
				messages = intent.getStringArrayListExtra("messages");
				icons = (ArrayList<Icon>) intent.getExtras().get("icons");
				sendTos = intent.getStringArrayListExtra("sendTos");
				replies = intent.getStringArrayListExtra("replies");
				senderColors = (ArrayList<Integer>) intent.getExtras().get("senderColors");
				buttonIcons = (ArrayList<Icon>) intent.getExtras().get("bIcons");
				buttonTexts = (ArrayList<String>) intent.getExtras().get("bTexts");
				imgs = (ArrayList<Bitmap>) intent.getExtras().get("images");
				autoSends = (ArrayList<Boolean>)intent.getExtras().get("autoSends");
				sendButtonClicks = (ArrayList<Boolean>)intent.getExtras().get("sendButtonClicks");
				seens = (ArrayList<Boolean>)intent.getExtras().get("seens");
				chatRemoves = (ArrayList<Boolean>)intent.getExtras().get("chatRemoves");
				chatSpams = (ArrayList<Boolean>)intent.getExtras().get("chatSpams");
				chatNotSpams = (ArrayList<Boolean>)intent.getExtras().get("chatNotSpams");
				chatListStates = (ArrayList<Parcelable>)intent.getExtras().get("chatListStates");

				if(mWindowManager != null)
				{
					//adding views to window according to priority
					mWindowManager.addView(mBlackBackView, mBlackBackViewParams);
					mWindowManager.addView(iconProjection, iconProjectionParams);
					mWindowManager.addView(iconList, iconListParams);
					mWindowManager.addView(chatProjectionDown, chatProjectionDownParams);
					mWindowManager.addView(chatProjectionUp, chatProjectionUpParams);
					mWindowManager.addView(chatListView, mExpandedViewParams);
					mWindowManager.addView(mRemoveView, mRemoveViewParams);
					mWindowManager.addView(mChatHeadView, mChatHeadViewParams);
				}

				for(boolean chatRemove : chatRemoves)
					if(chatRemove)
						chatRemovesCount++;

				totalVisibleIcons = senders.size();
				totalVisibleChats = senders.size();

				onChatDataChange();

				refreshNotification();
			}

			else

			if(type.equals("error"))
				Toast.makeText(getApplicationContext(), intent.getStringExtra("error"), Toast.LENGTH_LONG).show();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void onThreadLoadFinish()
	{
		/*for(int chatViewIndex = 0;chatViewIndex < chatViewsCount;chatViewIndex++)
		 {
		 if(replyViews.get(chatViewIndex) != null)
		 {
		 int chatIndex = chatInFocus == CHAT_ALL? chatViewIndex : chatInFocus;

		 String reply = replyViews.get(chatViewIndex).getText().toString();
		 String replyToSet = replies.get(chatIndex);

		 if(!reply.equals(replyToSet))
		 {
		 replies.set(chatIndex, replyToSet = (!replyToSet.equals(PENDING) && !replyToSet.equals(LOADING))? replyToSet : reply);
		 replyViews.get(chatViewIndex).setText(replyToSet);
		 }
		 else
		 if(replyToSet.equals(PENDING) || replyToSet.equals(LOADING))
		 {
		 replies.set(chatIndex, reply);
		 replyViews.get(chatViewIndex).setText(replyToSet);
		 }
		 }
		 else
		 break;
		 }*/
	}

	private void restoreReplies()
	{
		for(int chatViewIndex = 0;chatViewIndex < chatViewsCount;chatViewIndex++)
		{
			if(replyViews.get(chatViewIndex) != null)
			{
				int chatIndex = chatInFocus <= CHAT_ALL? chatViewIndex : chatInFocus;

				String reply = replyViews.get(chatViewIndex).getText().toString();

				if(!reply.equals(PENDING) && !reply.equals(LOADING))
					replies.set(chatIndex, reply);
			}
			else
				break;
		}
	}

	private void onChatDataChange()
	{
		if(visible)
		{
			if(isExpanded)
			{
				if(showingChat)//chatList.getAdapter() != null)
				{
					restoreReplies();
					((BaseAdapter)chatList.getAdapter()).notifyDataSetChanged();
				}
				if(iconList.getAdapter() != null)
					((BaseAdapter)iconList.getAdapter()).notifyDataSetChanged();

				int chatCountNoRemoves = 0;
				for(boolean chatRemoved : chatRemoves)
					if(!chatRemoved)
						chatCountNoRemoves++;

				if(chatCountNoRemoves <= 0 || (showingChat && (chatInFocus == CHAT_ALL? (chatViewsCount <= 0) : (replyViews.get(0) == null))))
				{
					sleepTimer.start();
					mBlackBackView.setAlpha(0f);
					mBlackBackView.setVisibility(View.GONE);
					iconProjection.setVisibility(View.GONE);
					iconList.setVisibility(View.GONE); iconList.setAdapter(null);
					if(showingChat)
						chatListStates.set(chatInFocus+2, chatList.onSaveInstanceState());
					chatListView.setVisibility(View.GONE); //chatList.setAdapter(null);
					isExpanded = false;
					chatProjectionUp.setVisibility(View.GONE);
					chatProjectionDown.setVisibility(View.GONE);
					if(showingChat)
					{
						showingChat = false;
						onChatClose(chatInFocus);
					}
					chatHead.setImageResource(R.drawable.ic_logo);
					chatHead.setAlpha(1f);

					refreshChatCircles();
				}
			}
		}
	}

	private void onChatClose(int chatInFocus)
	{
		if(chatInFocus <= CHAT_ALL)
		{
			for(int chatIndex = 0;chatIndex < senders.size();chatIndex++)
			{
				if(seens.get(chatIndex) != null && !seens.get(chatIndex))
				{
					seens.set(chatIndex, null);
				}
			}
		}
		else
		{
			if(seens.get(chatInFocus) != null && !seens.get(chatInFocus))
			{
				seens.set(chatInFocus, null);
			}
		}

		onChatDataChange();
	}

	private int[] getSendCounts()
	{
		int sendsTotal = 0;
		int autoSendsTotal = 0;
		int sendRemovesTotal = 0;
		for(int s = 0;s < senders.size();s++)
		{
			if(autoSends.get(s) || sendButtonClicks.get(s))
			{
				sendsTotal++;
				if(autoSends.get(s))
					autoSendsTotal++;
				if(chatRemoves.get(s))
					sendRemovesTotal++;
			}
		}

		return new int[]{sendsTotal, autoSendsTotal, sendRemovesTotal};
	}

	private void refreshChatCircles()
	{
		int[] counts = getSendCounts();
		int sendsTotal = counts[0];
		int autoSendsTotal = counts[1];
		int sendRemovesTotal = counts[2];

		if(chatCircleMessages != null && chatCircleReplies != null)
		{
			chatCircleMessages.setText((senders.size() - chatRemovesCount) < 10? " " + String.valueOf((senders.size() - chatRemovesCount)) : (senders.size() - chatRemovesCount) < 1000? String.valueOf((senders.size() - chatRemovesCount)) : (senders.size() - chatRemovesCount) < 1000000? String.valueOf((senders.size() - chatRemovesCount)/1000f).substring(0, 3) + "K" : (senders.size() - chatRemovesCount) < 1000000000? String.valueOf((senders.size() - chatRemovesCount)/1000000f).substring(0, 3) + "M" : String.valueOf((senders.size() - chatRemovesCount)/1000000000f).substring(0, 3) + "B");
			chatCircleReplies.setText((sendsTotal - sendRemovesTotal) < 10? " " + String.valueOf((sendsTotal - sendRemovesTotal)) : (sendsTotal - sendRemovesTotal) < 1000? String.valueOf((sendsTotal - sendRemovesTotal)) : (sendsTotal - sendRemovesTotal) < 1000000? String.valueOf((sendsTotal - sendRemovesTotal)/1000f).substring(0, 3) + "K" : (sendsTotal - sendRemovesTotal) < 1000000000? String.valueOf((sendsTotal - sendRemovesTotal)/1000000f).substring(0, 3) + "M" : String.valueOf((sendsTotal - sendRemovesTotal)/1000000000f).substring(0, 3) + "B");

			if((senders.size() - chatRemovesCount) > 99)
				chatCircleMessages.setTextSize(chatHeadWidth/25);
			else
				chatCircleMessages.setTextSize(chatHeadWidth/15);
			
			if((sendsTotal - sendRemovesTotal) > 99)
				chatCircleReplies.setTextSize(chatHeadWidth/25);
			else
				chatCircleReplies.setTextSize(chatHeadWidth/15);

			if(!isExpanded && !moving)
			{
				chatCircleReplies.setVisibility(View.VISIBLE);
				chatCircleMessages.setVisibility(View.VISIBLE);
			}
		}
	}

	private void refreshNotification()
	{
		int[] counts = getSendCounts();
		int sendsTotal = counts[0];
		int autoSendsTotal = counts[1];

		String notifyStrMsg = String.valueOf((senders != null) ? senders.size()-chatRemovesCount : 0) + " " + "messages";
		String notifyStrTxt = "from";
		if(senders != null)
			for(int s = 0;s<senders.size();s++)
				if(!chatRemoves.get(s))
					notifyStrTxt += " " + senders.get(s) + ",";
		notifyStrTxt = notifyStrTxt.substring(0, notifyStrTxt.length()-1);
		int spamsCount = 0;
		for(boolean isSpam : chatSpams)
			if(isSpam)
			{
				spamsCount++;
			}
		for(Boolean notSpam : chatNotSpams)
			if(notSpam!=null && !notSpam)
			{
				spamsCount++;
			}
		String notifyStrRly = String.valueOf(spamsCount) + " " + "spams";
		notification.setContentText(notifyStrTxt);
		notification.setContentTitle(notifyStrMsg);
		notification.setSubText(notifyStrRly);
		notification.setTicker(notifyStrMsg + " | " + notifyStrRly);
		startForeground(notificationId, notification.build(), foregroundType);
	}

	private int getChatViewIndex(int chatIndex)
	{
		for(int i=0;i<chatViewsCount;i++)
		{
			if(getChatIndexNoRemoves(i, 0, senders.size()) == chatIndex)
			{
				return i;
			}
		}
		return 0;
	}

	private int getChatIndexNoRemoves(int chatIndexWithRemoves, int start, int end)
	{
		int chatIndexTmp = chatIndexWithRemoves;

		int chatCountNoRemoves = -1;
		boolean ascend  = end >= start;

		if(ascend)
		{
			for(int ci = start;ci < end;ci++)
			{
				if(!chatRemoves.get(ci))
				{
					chatCountNoRemoves++;
					if(chatCountNoRemoves == chatIndexWithRemoves)
					{
						chatIndexTmp = ci;
						break;
					}
				}
			}
		}
		else
		{
			for(int ci = start;ci > end;ci--)
			{
				if(!chatRemoves.get(ci))
				{
					chatCountNoRemoves++;
					if(chatCountNoRemoves == chatIndexWithRemoves)
					{
						chatIndexTmp = ci;
						break;
					}
				}
			}
		}

		return chatIndexTmp;
	}

	private int getChatIndexOfRemoves(int chatIndexWithRemoves, int start, int end)
	{
		int chatIndexTmp = chatIndexWithRemoves;

		int chatCountOfRemoves = -1;
		boolean ascend  = end >= start;

		if(ascend)
		{
			for(int ci = start;ci < end;ci++)
			{
				if(chatRemoves.get(ci))
				{
					chatCountOfRemoves++;
					if(chatCountOfRemoves == chatIndexWithRemoves)
					{
						chatIndexTmp = ci;
						break;
					}
				}
			}
		}
		else
		{
			for(int ci = start;ci > end;ci--)
			{
				if(chatRemoves.get(ci))
				{
					chatCountOfRemoves++;
					if(chatCountOfRemoves == chatIndexWithRemoves)
					{
						chatIndexTmp = ci;
						break;
					}
				}
			}
		}

		return chatIndexTmp;
	}

	public boolean sendTextMessage(String message, String pkg, String sendTo)
	{
        Intent sendIntent = new Intent(Intent.ACTION_SEND)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
				.setType("text/plain")
				.putExtra(Intent.EXTRA_TEXT, message)
				.setPackage(pkg);
        
		Intent smsIntent = new Intent(Intent.ACTION_SENDTO, 
										 Uri.parse("smsto:"+sendTo))
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
			.addCategory(Intent.CATEGORY_DEFAULT)
        	.addCategory(Intent.CATEGORY_BROWSABLE)//Signal, KakaoTalk, FB-Messenger, ChatON
			.putExtra("sms_body", message)
			.setPackage(pkg);//complete intent for sending a basic text message, anything additional is app specific requirement

		Intent viewIntent = new Intent(Intent.ACTION_VIEW)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
				.setPackage(pkg);
        
        PackageManager packageManager = getPackageManager();

        /*order:
        	1.SENDTO:WhatsApp, Viber(sms), Signal(sms), KakaoTalk(sms), ChatON(sms)
			2.VIEW  :Skype, Instagram(username), Zalo
			3.SEND  :FB-Messenger(sms), Telegram, WeChat, Line, imo, Instagram(name), SnapChat, Twitter, Discord, Plus Messenger, bip, kik
		*/

		if(pkg.equals("com.whatsapp"))
		{
			if(sendTo!=null)
			{
				sendTo = sendTo.replace("+", "").replace(" ", "");

				sendIntent.putExtra("jid", sendTo + "@s.whatsapp.net");
				
				if(sendIntent.resolveActivity(packageManager) != null)
				{
					startActivity(sendIntent);
					return true;
				}
				else
				{
			        String url = "whatsapp://send?phone=" + sendTo + "&text=" + URLEncoder.encode(message);
			        viewIntent.setData(Uri.parse(url));
	        
					if(viewIntent.resolveActivity(packageManager) != null)
					{
						startActivity(viewIntent);
						return true;
					}
					else
					{
						try
						{
							url = "https://api.whatsapp.com/send?phone=" + sendTo + "&text=" + URLEncoder.encode(message, "UTF-8");
							viewIntent.setData(Uri.parse(url));

							if(viewIntent.resolveActivity(packageManager) != null)
							{
								startActivity(viewIntent);
								return true;
							}
						}
						catch (UnsupportedEncodingException e)
						{}
					}
				}
			}
		}
        
        if(pkg.equals("com.facebook.orca"))
        {
        	/*viewIntent.setData(Uri.parse("fb-messenger://user/100005727832736"));//Here 100005727832736 is the user id of the person who you want to message to
        	if(viewIntent.resolveActivity(getPackageManager()) != null)
			{
				startActivity(viewIntent);
				return true;
			}*/
			//Toast.makeText(getApplicationContext(), "sendTo:"+sendTo, Toast.LENGTH_LONG).show();
        }

		if(pkg.equals("org.telegram.messenger"))
		{
            /*viewIntent.setData(Uri.parse("https://t.me/USER_NAME"));
            if(viewIntent.resolveActivity(packageManager) != null)
            {
                startActivity(viewIntent);
                return true;
            }*/
        }

		if(pkg.equals("com.viber.voip"))
		{
			if(sendTo!=null)
			{
				smsIntent.putExtra("address", sendTo);
				smsIntent = smsIntent.setAction(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, message);
				smsIntent.removeCategory(Intent.CATEGORY_BROWSABLE);
			}
		}
        
        if(pkg.equals("com.zing.zalo"))
        {
        	if(sendTo!=null)
        	{
				viewIntent.addCategory(Intent.CATEGORY_DEFAULT)
	        	.addCategory(Intent.CATEGORY_BROWSABLE)
	        	.setData(Uri.parse("tel:"+sendTo));
	        	if(viewIntent.resolveActivity(packageManager) != null)
				{
					startActivity(viewIntent);
					return true;
				}
			}
        }

        if(pkg.equals("jp.naver.line.android"))
        {
        	try{
	        	viewIntent.setData(Uri.parse("https://line.me/R/share?text=" + URLDecoder.decode(message, "UTF-8")));
	        	if(viewIntent.resolveActivity(getPackageManager()) != null)
				{
					startActivity(viewIntent);
					return true;
				}
			}
			catch(UnsupportedEncodingException e)
			{}
        }

        if(pkg.equals("com.skype.raider"))
        {
        	if(sendTo!=null)
        	{
	        	viewIntent.setData(Uri.parse("skype:" + sendTo + "?chat"));
	        	if(viewIntent.resolveActivity(getPackageManager()) != null)
				{
					Toast.makeText(getApplicationContext(), sendTo, Toast.LENGTH_LONG).show();
					startActivity(viewIntent);
					return true;
				}
			}
        }

        if(pkg.equals("com.instagram.android"))
        {
        	if(!sendTo.contains(" "))
        	{
	        	viewIntent.setData(Uri.parse("http://instagram.com/_u/"+sendTo));
	        	if(viewIntent.resolveActivity(getPackageManager()) != null)
				{
					startActivity(viewIntent);
					return true;
				}
			}
        }

        if(pkg.equals("com.twitter.android"))
        {
        	sendIntent.setClassName("com.twitter.android", "com.twitter.app.dm.DMActivity");
        }

        //if(pkg.equals("com.snapchat.android"))
        //{
        	/*viewIntent.setData(Uri.parse("https://snapchat.com/add/" + snapchatId));
        	if(viewIntent.resolveActivity(getPackageManager()) != null)
			{
				startActivity(viewIntent);
				return true;
			}
			else{*/
        		/*ComponentName intentComponent = new ComponentName("com.snapchat.android", "com.snapchat.android.LandingPageActivity");
            	sendIntent.setComponent(intentComponent);

            	if(sendIntent.resolveActivity(getPackageManager()) != null)
				{
					startActivity(sendIntent);
					return true;
				}
			//}
        }*/

        if(sendTo!=null)
        {
        	if(smsIntent.resolveActivity(getPackageManager()) != null)
			{
				startActivity(smsIntent);
				return true;
			}
		}
		if(sendIntent.resolveActivity(getPackageManager()) != null)
		{
			startActivity(sendIntent);
			return true;
		}
		else
			return false;
	}

	public String getSendTo(String sender, String pkg)
	{
		Bundle groupConStrs = new Bundle();
		groupConStrs.putCharSequence("com.whatsapp", "@");
		groupConStrs.putCharSequence("com.viber.voip", "@");
		groupConStrs.putCharSequence("org.thoughtcrime.securesms", "@");
		groupConStrs.putCharSequence("com.kakao.talk", "@");
		groupConStrs.putCharSequence("com.zing.zalo", "@");
		groupConStrs.putCharSequence("com.instagram.android", "@");
		groupConStrs.putCharSequence("com.sec.chaton", "@");
		if(!(pkg.equals("com.whatsapp") || pkg.equals("com.viber.voip") || pkg.equals("org.thoughtcrime.securesms") || pkg.equals("com.kakao.talk") || pkg.equals("com.zing.zalo") || pkg.equals("com.instagram.android") || pkg.equals("com.sec.chaton")))
			groupConStrs.putCharSequence(pkg, "@");
		
		if(pkg.equals("com.skype.raider"))
		{
		    Cursor c = getApplicationContext().getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Contacts.Data.MIMETYPE + "=?",
		            new String[] { "vnd.android.cursor.item/com.skype.android.skypecall.action" }, null);

		    while (c != null && c.moveToNext()) {
		        String primary = c.getString(c.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));
		        String alternate = c.getString(c.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_ALTERNATIVE));
		        if(primary.equalsIgnoreCase(sender) || alternate.equalsIgnoreCase(sender)) {
		            String username = c.getString(c.getColumnIndex(ContactsContract.Data.DATA1));
		            c.close();
		            return username;
		        }
		    }
		    c.close();
		    return null;
		}

		if(pkg.equals("com.instagram.android"))
			return sender.split((String)groupConStrs.getCharSequence(pkg))[1].trim();

		//smsIntent
		if(sender.startsWith("+"))
			return sender.split((String)groupConStrs.getCharSequence(pkg))[0].trim();
		else
			return getNumber(sender.split((String)groupConStrs.getCharSequence(pkg))[0].trim());
	}

	public String getNumber(String name)
	{
		Cursor c = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
											  new String[]{ ContactsContract.CommonDataKinds.Phone.CONTACT_ID }, 
											  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?",
											  new String[]{ name },
											  null);
		c.moveToFirst();
		String id = c.getString(0);
		c.close();

		Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
												   new String[]{ ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER }, 
												   ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", 
												   new String[]{ id }, null);

		cursor.moveToFirst();
		String number = cursor.getString(0);
		cursor.close();

		return number;
	}

	public boolean requestReplyIntents(int chatIndex, String sender, String message, String reply, String id)
	{
		try
		{
			Intent reqIntent = new Intent(this, NotificationScannerService.class);
			reqIntent.putExtra("type", "replyIntentsReq");
			reqIntent.putExtra("chatIndex", chatIndex);
			reqIntent.putExtra("id", id);
			reqIntent.putExtra("sender", sender);
			reqIntent.putExtra("message", message);
			reqIntent.putExtra("reply", reply);
			startService(reqIntent);
			return true;
		}catch(Exception e)
		{ 
			return false;
		}
		//further, notification listener will send pending intent and reply intent, which is processed in "replyIntentsRes"(search for it above)
	}

	public boolean requestButtonIntents(String id)
	{
		try
		{
			Intent reqIntent = new Intent(this, NotificationScannerService.class);
			reqIntent.putExtra("type", "buttonIntentsReq");
			reqIntent.putExtra("id", id);
			startService(reqIntent);
			return true;
		}catch(Exception e)
		{ 
			return false;
		}
	}
    
    public boolean requestProfileIntents(int chatIndex, String id)
	{
		try
		{
			Intent reqIntent = new Intent(this, NotificationScannerService.class);
			reqIntent.putExtra("type", "profileIntentsReq");
			reqIntent.putExtra("id", id);
			reqIntent.putExtra("chatIndex", chatIndex);
			startService(reqIntent);
			return true;
		}catch(Exception e)
		{ 
			return false;
		}
	}
    
	public int distance(int x1, int y1, int x2, int y2)
	{
		//glad my maths book is of some use to me in the below way xD
		//distance formula(std 10th)
		return (int) Math.sqrt((double)((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
	}

	public int getNum(int num)
	{
		if(num >= 0)
			return num;
		else
			return -num;
	}

	public boolean writeToFile(File file, String[] data) throws IOException
	{
		FileOutputStream outputStream = new FileOutputStream(file, file.exists());
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

		for(String dt : data)
		{
			bufferedWriter.write(dt);
			bufferedWriter.newLine();
		}

		bufferedWriter.close();

		return true;
	}

	public String[] readFromFile(File file, String SEPARATOR) throws IOException
	{
		FileInputStream inputStream = new FileInputStream(file);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		if(SEPARATOR.equals("SEPARATOR_NEW_LINE"))
		{
			ArrayList data = new ArrayList<String>();
			String line = "";
			while((line = bufferedReader.readLine()) != null)
				data.add(line);

			bufferedReader.close();

			return (String[])data.toArray(new String[]{});

		}
		else
		{
			String data = "";
			String line = "";
			while((line = bufferedReader.readLine()) != null)
				data += line;

			bufferedReader.close();

			return data.split(SEPARATOR);
		}
	}

	public void restartSelf()
	{
		created = false;
		updated = false;
		visible = false;

		Intent initIntent = new Intent(this, ChatHeadService.class);
		initIntent.putExtra("type", "init");
		startService(initIntent);

		Intent restartIntent = new Intent(this, ChatHeadService.class);
		restartIntent.putExtra("type", "restart");
		restartIntent.putExtra("pkgs", pkgs);
		restartIntent.putExtra("ids", ids);
		restartIntent.putExtra("senders", senders);
		restartIntent.putExtra("messages", messages);
		restartIntent.putExtra("icons", icons);
		restartIntent.putExtra("sendTos", sendTos);
		restartIntent.putExtra("replies", replies);
		restartIntent.putExtra("senderColors", senderColors);
		restartIntent.putExtra("bIcons", buttonIcons);
		restartIntent.putExtra("bTexts", buttonTexts);
		restartIntent.putExtra("images", imgs);
		restartIntent.putExtra("autoSends", autoSends);
		restartIntent.putExtra("sendButtonClicks", sendButtonClicks);
		restartIntent.putExtra("seens", seens);
		restartIntent.putExtra("chatRemoves", chatRemoves);
		restartIntent.putExtra("chatSpams", chatSpams);
		restartIntent.putExtra("chatNotSpams", chatNotSpams);
		restartIntent.putExtra("chatListStates", chatListStates);
		startService(restartIntent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO: Implement this method
		if(created)
		{
			sleepShrinkTimer.cancel();
			sleepTimer.cancel();
			
			screenWidth = getResources().getDisplayMetrics().widthPixels;
			screenHeight = getResources().getDisplayMetrics().heightPixels;

			xdpi = getResources().getDisplayMetrics().xdpi;
			ydpi = getResources().getDisplayMetrics().ydpi;

			chatHeadWidth = (int)(xdpi / chatHeadSizeDiv);
			chatHeadHeight = chatHeadWidth;

			chatListWidth = screenWidth - chatHeadWidth * 2;
			chatListHeight = screenHeight - chatHeadHeight * 2;

			isToLeft = mChatHeadViewParams.x + chatHeadWidth / 2 < screenWidth / 2;
			
			if(!isToLeft)
				mChatHeadViewParams.x = screenWidth - chatHeadWidth;
			else
				mChatHeadViewParams.x = 0;

			if(mChatHeadViewParams.y + chatHeadHeight > screenHeight)
				mChatHeadViewParams.y = screenHeight - chatHeadHeight;
			else
			if(mChatHeadViewParams.y < 0)
				mChatHeadViewParams.y = 0;
			
			if(isShrinked)
			{
				chatCircleMessages.setVisibility(View.GONE);
				chatCircleReplies.setVisibility(View.GONE);
				sleepShrinkTimer.onFinish();
			}

			chatListView.setVisibility(View.GONE); //chatList.setAdapter(null);
			isExpanded = false;
			mBlackBackView.setVisibility(View.GONE);
			iconList.setVisibility(View.GONE); iconList.setAdapter(null);
			iconProjection.setVisibility(View.GONE);
			chatProjectionUp.setVisibility(View.GONE);
			chatProjectionDown.setVisibility(View.GONE);
			chatHead.setImageResource(R.drawable.ic_logo);
			chatHead.setAlpha(1f);
			chatHead.setScaleX(isToLeft? 1f : -1f);
			chatHead.setScaleY(1f);
			chatHead.setX(0);
			chatCircleMessages.setVisibility(View.VISIBLE);
			chatCircleReplies.setVisibility(View.VISIBLE);
			sleepTimer.start();
		}

		if(visible)
			mWindowManager.updateViewLayout(mChatHeadView, mChatHeadViewParams);

		super.onConfigurationChanged(newConfig);
	}

	@Override
    public void onDestroy()
	{
        super.onDestroy();

		if(voluntaryDestroing)
		{
        	if(mChatHeadView != null)		mWindowManager.removeView(mChatHeadView);
			if(mRemoveView !=  null)		mWindowManager.removeView(mRemoveView);
			if(chatListView != null)		mWindowManager.removeView(chatListView);
			if(mBlackBackView != null)		mWindowManager.removeView(mBlackBackView);
			if(iconProjection != null)		mWindowManager.removeView(iconProjection);
			if(iconList != null)			mWindowManager.removeView(iconList);
			if(chatProjectionDown != null)	mWindowManager.removeView(chatProjectionDown);
			if(chatProjectionUp != null)	mWindowManager.removeView(chatProjectionUp);

			ArrayList<String> ignores = new ArrayList<String>();
			ArrayList<String>sends = new ArrayList<String>();

			for(int i = 0;i < senders.size();i++)
			{
				if(!sendButtonClicks.get(i) && !autoSends.get(i))
					ignores.add(senders.get(i));
				else
					sends.add(senders.get(i));
			}

			try
			{
				int igLen = ignoredNames.length;
				int snLen = sentNames.length;

				for(int i = 0;i < sends.size();i++)
				{
					for(int j = 0;j < ignoredNames.length;j++)
					{
						if(sends.get(i).equals(ignoredNames[j]))
						{
							ignoredNames[j] = "";
							igLen--;
							break;
						}
					}
				}

				for(int i = 0;i < ignores.size();i++)
				{
					for(int j = 0;j < sentNames.length;j++)
					{
						if(ignores.get(i).equals(sentNames[j]))
						{
							sentNames[j] = "";
							snLen--;
							break;
						}
					}
				}

				String[] tmpIgnoredNames = new String[igLen];
				String[] tmpSentNames = new String[snLen];

				for(int ig = 0;ig < igLen;ig++)
					if(!ignoredNames[ig].equals("") && !ignoredNames[ig].equals(" "))
						tmpIgnoredNames[ig] = ignoredNames[ig];
				for(int sn = 0;sn < snLen;sn++)
					if(!sentNames[sn].equals("") && !sentNames[sn].equals(" "))
						tmpSentNames[sn] = sentNames[sn];

				ignoredsFile.delete();
				ignoredsFile.createNewFile();
				writeToFile(ignoredsFile, ignoredNames);

				sentsFile.delete();
				sentsFile.createNewFile();
				writeToFile(sentsFile, sentNames);

				writeToFile(sentsFile, sends.toArray(new String[]{}));
				writeToFile(ignoredsFile, ignores.toArray(new String[]{}));
			}catch(Exception e){}

			if(showingChat)
			{
				showingChat = false;
				onChatClose(chatInFocus);
			}
			if(isExpanded)
				isExpanded = false;
			updated = false;
			visible = false;
			created = false;

			Intent intentDestroyed = new Intent(ChatHeadService.this, NotificationScannerService.class);
			intentDestroyed.putExtra("type", "destroyed");
			startService(intentDestroyed);
		}
		else
			restartSelf();

		loadThread.interrupt();
    }

    public String[] getMissing(String[] from, String[] to)
    {
    	ArrayList<String> missing = new ArrayList<String>();
    	for(int i=0;i<from.length;i++)
    	{
    		boolean isMissing = true;
    		for(int j=0;j<to.length;j++)
    		{
    			if(from[i].equals(to[j]))
    			{
    				isMissing = false;
    				break;
    			}
    		}
    		if(isMissing)
    			missing.add(from[i]);
    	}
    	return missing.toArray(new String[]{});
    }

	private void haptic(int duration, int amp) {
		if(hapticsEnabled)
		    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
		        VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
		        Vibrator vibrator = vibratorManager.getDefaultVibrator();
		        vibrator.vibrate(VibrationEffect.createOneShot(duration, amp));
		    }
		    else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
		        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		        vibrator.vibrate(VibrationEffect.createOneShot(duration, amp));
		    } else {
		        // API < 26
		        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		        vibrator.vibrate(duration);
		    }
	}

	public boolean writeToStream(OutputStream outputStream, String[] data, String SEPARATOR) throws IOException
	{
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

		for(String dt : data)
		{
			bufferedWriter.write(dt);
			if(SEPARATOR.matches("SEPARATOR_NEW_LINE"))
				bufferedWriter.newLine();
			else 
				bufferedWriter.write(SEPARATOR);
		}

		bufferedWriter.close();

		return true;
	}

	public String[] readFromStream(InputStream is, String SEPARATOR) throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

		if(SEPARATOR.matches("SEPARATOR_NEW_LINE"))
		{
			ArrayList data = new ArrayList<String>();
			String line = "";
			while((line = bufferedReader.readLine()) != null)
				data.add(line);
			
			bufferedReader.close();

			return (String[])data.toArray(new String[]{});

		}
		else
		{
			String data = "";
			String line = "";
			while((line = bufferedReader.readLine()) != null)
				data += line;

			bufferedReader.close();

			return data.split(SEPARATOR);
		}
    }

	public String readFromStream(InputStream is) throws IOException
	{
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

					String data = "";
                    String line;
                    if ((line = br.readLine()) != null)
                        data+=line+"\n";
                    br.close();
        return data;
    }
}
