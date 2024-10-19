package com.prajwal.holochat;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.PermissionChecker;

import java.io.*;
import java.util.*;

public class AppActivity extends Activity
{
	private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 8074;
	private static final int CODE_NOTIFICATION_ACCESS = 8421;
	private static final int CODE_BATTERY_OPTIMIZATION = 8021;
	private static final int CODE_COMMON = 2;
	String[] permissions = new String[]{
		Manifest.permission.READ_CONTACTS
		//Manifest.permission.WRITE_EXTERNAL_STORAGE
	};
	String[] permissionsReason = new String[]{
		"\'Read Contacts\' permission is required for messaging a contact"
		//"\'Write External Storage\' permission is required for storing app files"
	};
	ArrayList<String> pendingPermissions;

	Bot bot;
	Random random;

	int screenWidth;
	int screenHeight;
	float screenDensity;
	float xdpi;
	float ydpi;
	int MATCH_PARENT;
	int WRAP_CONTENT;
	int baseButtonsWidth;
	int baseButtonsHeight;
	float chatHeadSizeDiv;
	int chatHeadSensitivity;
	boolean hapticsEnabled;
	boolean OTPProtectionEnabled;
	boolean keepNotifications;

	RelativeLayout baseLayout;
	Button[] baseButtons;
	RelativeLayout.LayoutParams[] baseButtonsParams;
	View[] baseButtonsExpanded;
	RelativeLayout.LayoutParams[] baseButtonsExpandedParams;
	ImageView closeButton;
	boolean reverse = true;

	int columnsTotal;
	int rowsTotal;
	int baseButtonsTotal;

	File dataDir;
	File CHDataDir;
	File autoSendTosFile;
	File autoSendTosUFile;
	File autoSendMsgsFile;
	File autoSendMsgsUFile;
	File chatHeadSizeDivFile;
	File sentsFile;
	File ignoredsFile;
	File chatHeadSensitivityFile;
	File hapticsEnabledFile;
	File OTPProtectionEnabledFile;
	File keepNotificationsFile;
	File NLDataDir;
	File tgtAppsPkgFile;
	File ignoreTitlesFile;
	File ignoreTextsFile;
	File botDataDir;
	File SpamDir;
	File absDir;

	ArrayList<String> tgtAppsPkg;
	ArrayList<String> ignoreTitles;
	ArrayList<String> ignoreTexts;
	List<ApplicationInfo> appsInfo;
	ArrayList<String> installedAppsName;
	ArrayList<String> appsName;
	ArrayList<String> installedAppsPkg;
	ArrayList<String> appsPkg;
	ArrayList<Drawable> installedAppsIcon;
	ArrayList<Drawable> appsIcon;
	ArrayList<String> autoSendTos;
	ArrayList<String> autoSendTosU;
	ArrayList<String> autoSendMsgs;
	ArrayList<String> autoSendMsgsU;
	ArrayList<String> sents;
	ArrayList<String> ignoreds;
	ArrayList<String[]>[] abstracts;
	ArrayList<String>[] abstractsFileName;
	int REQ = 0;
	int RES = 1;
	String splitStr = " : ";
	boolean chsCreated = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		random = new Random();

		MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
		WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

		screenWidth = getResources().getDisplayMetrics().widthPixels;
		screenHeight = getResources().getDisplayMetrics().heightPixels;
		screenDensity = getResources().getDisplayMetrics().density;

		xdpi = getResources().getDisplayMetrics().xdpi;
		ydpi = getResources().getDisplayMetrics().ydpi;

		int paddingLeft = screenWidth/100;
		int paddingTop = screenHeight/100;
		int paddingRight = screenWidth/100;
		int paddingBottom = screenHeight/100;

		screenWidth -= paddingLeft + paddingRight;
		screenHeight -= paddingTop + paddingBottom;

		dataDir = getFilesDir();
		CHDataDir = new File(dataDir, "ChatHeadData");
		autoSendTosFile = new File(CHDataDir, "autoSendTos");
		autoSendTosUFile = new File(CHDataDir, "autoSendTosU");
		autoSendMsgsFile = new File(CHDataDir, "autoSendMsgs");
		autoSendMsgsUFile = new File(CHDataDir, "autoSendMsgsU");
		chatHeadSizeDivFile = new File(CHDataDir, "chatHeadSizeDiv");
		sentsFile = new File(CHDataDir, "sents");
		ignoredsFile = new File(CHDataDir, "ignoreds");
		chatHeadSensitivityFile = new File(CHDataDir, "chatHeadSensitivity");
		hapticsEnabledFile = new File(CHDataDir, "hapticsEnabled");
		OTPProtectionEnabledFile = new File(CHDataDir, "OTPProtectionEnabled");
		keepNotificationsFile = new File(CHDataDir, "keepNotifications");
		NLDataDir = new File(dataDir, "NotificationListenerData");
		tgtAppsPkgFile = new File(NLDataDir, "tgtAppsPkg");
		ignoreTitlesFile = new File(NLDataDir, "ignoreTitles");
		ignoreTextsFile = new File(NLDataDir, "ignoreTexts");
		botDataDir = new File(dataDir, "BotData");
		SpamDir = new File(getExternalFilesDir(null), "Spam");
		absDir = new File(botDataDir, "abstracts");

		//requesting permission grant
		try
		{
			pendingPermissions = new ArrayList<String>();
			for(String permission : permissions)
				if(PermissionChecker.checkSelfPermission(this, permission) !=  PermissionChecker.PERMISSION_GRANTED)
					pendingPermissions.add(permission);

			if(!Settings.canDrawOverlays(this))
			{
				Intent intentPermissionDrawOverOtherApps = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
				startActivityForResult(intentPermissionDrawOverOtherApps, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
			}else
			if(!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName()))
			{
				Intent intentPermissionNotificationAccess = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
				startActivityForResult(intentPermissionNotificationAccess, CODE_NOTIFICATION_ACCESS);
			}
			else
			/*if (!((PowerManager)getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(getPackageName())) 
			{
			    Intent intentPermissionDisableBatteryOptimization = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));
			    startActivityForResult(intentPermissionDisableBatteryOptimization, CODE_BATTERY_OPTIMIZATION);
			}else*/
			if(pendingPermissions.size() > 0)
			{
				requestPermissions(pendingPermissions.toArray(new String[]{}), CODE_COMMON);
			}

			if(NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())
				&& Settings.canDrawOverlays(this))
			{
				tgtAppsPkg = new ArrayList<String>();
				for(String tgtAppPkg : readFromFile(tgtAppsPkgFile, "SEPARATOR_NEW_LINE"))
					tgtAppsPkg.add(tgtAppPkg);

				ignoreTitles = new ArrayList<String>();
				for(String ignoreTitle : readFromFile(ignoreTitlesFile, "SEPARATOR_NEW_LINE"))
					ignoreTitles.add(ignoreTitle);

				ignoreTexts = new ArrayList<String>();
				for(String ignoreText : readFromFile(ignoreTextsFile, "SEPARATOR_NEW_LINE"))
					ignoreTexts.add(ignoreText);

				autoSendTos = new ArrayList<String>();
				for(String autoSendTo : readFromFile(autoSendTosFile, "SEPARATOR_NEW_LINE"))
					autoSendTos.add(autoSendTo);

				autoSendTosU = new ArrayList<String>();
				for(String autoSendToU : readFromFile(autoSendTosUFile, "SEPARATOR_NEW_LINE"))
					autoSendTosU.add(autoSendToU);

				autoSendMsgs = new ArrayList<String>();
				for(String autoSendMsg : readFromFile(autoSendMsgsFile, "SEPARATOR_NEW_LINE"))
					autoSendMsgs.add(autoSendMsg);

				autoSendMsgsU = new ArrayList<String>();
				for(String autoSendMsgU : readFromFile(autoSendMsgsUFile, "SEPARATOR_NEW_LINE"))
					autoSendMsgsU.add(autoSendMsgU);

				sents = new ArrayList<String>();
				for(String sent : readFromFile(sentsFile, "SEPARATOR_NEW_LINE"))
					sents.add(sent);

				ignoreds = new ArrayList<String>();
				for(String ignored : readFromFile(ignoredsFile, "SEPARATOR_NEW_LINE"))
					ignoreds.add(ignored);

				bot = new Bot(botDataDir);
				refreshAbstracts();
			}

		}catch(Exception e)
		{}//Toast.makeText(this, e.toString()+"\n\n"+e.getStackTrace()[0].toString(), Toast.LENGTH_LONG).show();}
		
		try
		{
			baseLayout = new RelativeLayout(this);

			try
			{
				chatHeadSizeDiv = Float.valueOf(readFromFile(chatHeadSizeDivFile, "SEPARATOR_NEW_LINE")[0]);
				chatHeadSensitivity = Integer.parseInt(readFromFile(chatHeadSensitivityFile, "SEPARATOR_NEW_LINE")[0]);
				hapticsEnabled = Boolean.parseBoolean(readFromFile(hapticsEnabledFile, "SEPARATOR_NEW_LINE")[0]);
				OTPProtectionEnabled = Boolean.parseBoolean(readFromFile(OTPProtectionEnabledFile, "SEPARATOR_NEW_LINE")[0]);
				keepNotifications = Boolean.parseBoolean(readFromFile(keepNotificationsFile, "SEPARATOR_NEW_LINE")[0]);
			}catch (IOException e)
			{}catch (NumberFormatException e)
			{}

			final int chatHeadSize = (int)(xdpi / chatHeadSizeDiv);

			baseButtonsTotal = 7;

			if(baseButtonsTotal % 2 == 0)
			{
				if(screenWidth > screenHeight)
				{
					rowsTotal = (int)(Math.round((Math.sqrt(baseButtonsTotal) * ((float)screenHeight/(float)screenWidth))));
					columnsTotal = baseButtonsTotal/rowsTotal;
				}
				else
				{
					columnsTotal = (int)(Math.round((Math.sqrt(baseButtonsTotal) * ((float)screenWidth/(float)screenHeight))));
					rowsTotal = baseButtonsTotal/columnsTotal;
				}
			}
			else
			{
				if(screenWidth > screenHeight)
				{
					rowsTotal = (int)(Math.round((Math.sqrt(baseButtonsTotal) * ((float)screenHeight/(float)screenWidth)))) + 1;
					columnsTotal = baseButtonsTotal/rowsTotal;
				}
				else
				{
					columnsTotal = (int)(Math.round((Math.sqrt(baseButtonsTotal) * ((float)screenWidth/(float)screenHeight)))) + 1;
					rowsTotal = baseButtonsTotal/columnsTotal;
				}
			}

			baseButtons = new Button[baseButtonsTotal];
			baseButtonsWidth = ((screenWidth+paddingLeft+paddingRight)/(columnsTotal));
			baseButtonsHeight = ((screenHeight+paddingTop+paddingBottom)/(rowsTotal));
			baseButtonsParams = new RelativeLayout.LayoutParams[baseButtonsTotal];
			//just for no typos
			final String CCH = "Create Chat Head";
			final String TA = "Target Apps";
			final String TUT = "Check Update";
			final String NW = "Not Working?";
			final String Su = "Contact";
			final String S = "Settings";
			final String[] baseButtonsText = new String[]{
				CCH,
				TA,
				TUT,
				NW,
				Su,
				S,
				""
			};
			final boolean[] baseButtonsExpandable = new boolean[]{
				false,
				true,
				false,
				true,
				false,
				true
			};
			baseButtonsExpanded = new View[baseButtonsTotal];
			baseButtonsExpandedParams = new RelativeLayout.LayoutParams[baseButtonsTotal];
			final int baseButtonsTransTime = 150;

			for(int bb = 0;bb < baseButtonsTotal;bb++)
			{
				baseButtons[bb] = new Button(this);
				baseButtons[bb].setText(baseButtonsText[bb]);
				baseButtons[bb].setTextColor(Color.WHITE);
				baseButtons[bb].setBackgroundResource(R.drawable.chat_holo);
				final int tmpbb = bb;

				baseButtons[tmpbb].setOnClickListener(new View.OnClickListener(){

						RelativeLayout.LayoutParams closeButtonParams;
						boolean baseButtonTransTimerFinished = true;

						//irrelatable
						boolean showSystemApps = false;

						CountDownTimer baseButtonTransTimer = new CountDownTimer(baseButtonsTransTime, 1){

							@Override
							public void onTick(long p1)
							{
								// TODO: Implement this method
								baseButtonTransTimerFinished = false;
								baseButtonsParams[tmpbb].setMargins((int)(((tmpbb%columnsTotal)) * baseButtonsWidth * ((reverse? 1f : 0f) + ((float)p1/(float)baseButtonsTransTime)*(reverse? -1f : 1f))),
																	(int)(((tmpbb%columnsTotal == 0? tmpbb/columnsTotal : (tmpbb-(tmpbb%columnsTotal))/columnsTotal)) * baseButtonsHeight * ((reverse? 1f : 0f) + ((float)p1/(float)baseButtonsTransTime)*(reverse? -1f : 1f))),
																	0,
																	0);

								int widthToScreenWidth = (int)((screenWidth + baseButtonsWidth*(columnsTotal+1)) * ((float)baseButtonsWidth/(float)(screenWidth-baseButtonsWidth)) * ((!reverse? 1f : 0f) + ((float)p1/(float)baseButtonsTransTime)*(!reverse? -1f : 1f)));
								int heightToScreenHeight = (int)((screenHeight + baseButtonsHeight*(rowsTotal+1)) * ((float)baseButtonsHeight/(float)(screenHeight-baseButtonsHeight)) * ((!reverse? 1f : 0f) + ((float)p1/(float)baseButtonsTransTime)*(!reverse? -1f : 1f)));

								baseButtonsParams[tmpbb].width = baseButtonsWidth + widthToScreenWidth;
								baseButtonsParams[tmpbb].height = baseButtonsHeight + heightToScreenHeight;

								baseLayout.updateViewLayout(baseButtons[tmpbb], baseButtonsParams[tmpbb]);
							}

							@Override
							public void onFinish()
							{
								// TODO: Implement this method
								if(reverse)
								{
									baseLayout.setPaddingRelative(0, 0, 0, 0);
									baseLayout.setBackgroundResource(0);
									baseButtonsParams[tmpbb].setMargins(((tmpbb%columnsTotal)) * baseButtonsWidth,
																		(tmpbb%columnsTotal == 0? tmpbb/columnsTotal : (tmpbb-(tmpbb%columnsTotal))/columnsTotal) * baseButtonsHeight,
																		0,
																		0);
									baseButtonsParams[tmpbb].width = baseButtonsWidth;
									baseButtonsParams[tmpbb].height = baseButtonsHeight;
									baseLayout.updateViewLayout(baseButtons[tmpbb], baseButtonsParams[tmpbb]);
									baseButtons[tmpbb].setVisibility(View.VISIBLE);
								}
								else
								{
									baseLayout.setPaddingRelative(paddingLeft, paddingTop, paddingRight, paddingBottom);
									baseLayout.setBackgroundResource(R.drawable.chat_holo);
									baseButtonsParams[tmpbb].setMargins(0, 0, 0, 0);
									baseButtonsParams[tmpbb].width = screenWidth;
									baseButtonsParams[tmpbb].height = screenHeight;
									baseLayout.updateViewLayout(baseButtons[tmpbb], baseButtonsParams[tmpbb]);

									if(baseButtonsText[tmpbb].equals(TA))
									{
										baseButtons[tmpbb].setVisibility(View.GONE);
										baseButtonsExpanded[tmpbb] = new LinearLayout(getApplicationContext());
										((LinearLayout)baseButtonsExpanded[tmpbb]).setOrientation(LinearLayout.VERTICAL);
										baseButtonsExpandedParams[tmpbb] = new RelativeLayout.LayoutParams(screenWidth, screenHeight);

										final ListView appList = new ListView(getApplicationContext());
										if(appsInfo == null)
										{
											appsInfo = getPackageManager().getInstalledApplications(0);
											installedAppsName = new ArrayList<String>();
											appsName = new ArrayList<String>();
											installedAppsPkg = new ArrayList<String>();
											appsPkg = new ArrayList<String>();
											installedAppsIcon = new ArrayList<Drawable>();
											appsIcon = new ArrayList<Drawable>();

											for(ApplicationInfo appInfo : appsInfo)
											{
												String name = appInfo.loadLabel(getPackageManager()).toString();
												String pkgName = appInfo.packageName;
												Drawable icon = appInfo.loadIcon(getPackageManager());

												appsName.add(name);
												appsPkg.add(pkgName);
												appsIcon.add(icon);

												if((appInfo.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) == 0)
												{
													installedAppsName.add(name);
													installedAppsPkg.add(pkgName);
													installedAppsIcon.add(icon);
												}
											}
										}

										final BaseAdapter appListAdapter = new BaseAdapter(){

											@Override
											public int getCount()
											{
												// TODO: Implement this method

												return showSystemApps? appsName.size() : installedAppsName.size();
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
											public View getView(final int i, View view, ViewGroup viewGroup)
											{
												// TODO: Implement this method
												view = new LinearLayout(getApplicationContext());
												((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL);

												final Switch checkSwitch = new Switch(getApplicationContext());
												checkSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
												checkSwitch.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
												checkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

														@Override
														public void onCheckedChanged(CompoundButton button, boolean isChecked)
														{
															// TODO: Implement this method
															if(isChecked)
															{
																checkSwitch.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
																tgtAppsPkg.add(showSystemApps? appsPkg.get(i) : installedAppsPkg.get(i));
															}
															else{
																checkSwitch.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
																tgtAppsPkg.remove(showSystemApps? appsPkg.get(i) : installedAppsPkg.get(i));
															}
														}
													});
												checkSwitch.setChecked(tgtAppsPkg.contains(showSystemApps? appsPkg.get(i) : installedAppsPkg.get(i)));
												final LinearLayout.LayoutParams checkSwitchParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
												checkSwitchParams.gravity = Gravity.CENTER;

												final ImageView icon = new ImageView(getApplicationContext());
												icon.setImageDrawable(showSystemApps? appsIcon.get(i) : installedAppsIcon.get(i));
												final LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams((int)(xdpi/2.3f), (int)(xdpi/2.3f));
												iconParams.gravity = Gravity.CENTER;

												final TextView appName = new TextView(getApplicationContext());
												appName.setTextAppearance(android.R.style.TextAppearance_Large);
												appName.setTextColor(Color.WHITE);
												appName.setTypeface(Typeface.DEFAULT_BOLD);
												appName.setText(" "+(showSystemApps? appsName.get(i) : installedAppsName.get(i))); 
												final LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
												nameParams.gravity = Gravity.CENTER;

												view.setOnClickListener(new View.OnClickListener(){

														@Override
														public void onClick(View p1)
														{
															// TODO: Implement this method
															checkSwitch.setChecked(!checkSwitch.isChecked());
														}
													});

												((LinearLayout)view).addView(checkSwitch, checkSwitchParams);
												((LinearLayout)view).addView(icon, iconParams);
												((LinearLayout)view).addView(appName, nameParams);

												return view;
											}
										};
										appList.setAdapter(appListAdapter);
										LinearLayout.LayoutParams appListParams = new LinearLayout.LayoutParams(MATCH_PARENT, screenHeight - Math.min(baseButtonsWidth, baseButtonsHeight)/2 - baseButtonsHeight/4);

										final LinearLayout ssa = new LinearLayout(getApplicationContext());
										final LinearLayout.LayoutParams showSystemAppsParams = new LinearLayout.LayoutParams(MATCH_PARENT, baseButtonsHeight/4);
										final CheckBox ssaCheck = new CheckBox(getApplicationContext());
										ssaCheck.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
										ssaCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

												@Override
												public void onCheckedChanged(CompoundButton p1, boolean p2)
												{
													// TODO: Implement this method
													showSystemApps = ssaCheck.isChecked();
													appListAdapter.notifyDataSetChanged();
													//appList.setAdapter(appListAdapter);
												}
											});
										final LinearLayout.LayoutParams ssaCheckParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
										ssaCheckParams.gravity = Gravity.CENTER;
										final TextView ssaText = new TextView(getApplicationContext());
										ssaText.setTextColor(Color.WHITE);
										ssaText.setText("Show System Apps");
										ssaText.setOnClickListener(new View.OnClickListener(){

												@Override
												public void onClick(View p1)
												{
													// TODO: Implement this method
													ssaCheck.setChecked(!ssaCheck.isChecked());
													showSystemApps = ssaCheck.isChecked();
													appListAdapter.notifyDataSetChanged();
													//appList.setAdapter(appListAdapter);
												}
											});
										final LinearLayout.LayoutParams ssaTextParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
										ssaTextParams.gravity = Gravity.CENTER;
										ssa.addView(ssaCheck, ssaCheckParams);
										ssa.addView(ssaText, ssaTextParams);

										Button doneButton = new Button(getApplicationContext());
										doneButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
										doneButton.setText("Done");
										doneButton.setOnClickListener(new View.OnClickListener(){

												@Override
												public void onClick(View p1)
												{
													// TODO: Implement this method
													if(baseButtonTransTimerFinished)
													{
														try
														{
															tgtAppsPkgFile.delete();
															tgtAppsPkgFile.createNewFile();
															writeToFile(tgtAppsPkgFile, tgtAppsPkg.toArray(new String[]{}), "SEPARATOR_NEW_LINE");

															Intent NLIntent = new Intent(AppActivity.this, NotificationScannerService.class);
															NLIntent.putExtra("type", "update");
															startService(NLIntent);
														}catch (IOException e)
														{}

														closeButton.performClick();

														for(int rbb = 0;rbb < baseButtonsTotal;rbb++)
															baseButtons[rbb].setVisibility(View.VISIBLE);

														baseLayout.removeView(closeButton);
														baseButtonsExpanded[tmpbb].setVisibility(View.GONE);
													}
												}
											});
										LinearLayout.LayoutParams doneButtonParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
										//doneButtonParams.gravity = Gravity.RIGHT;

										((LinearLayout)baseButtonsExpanded[tmpbb]).addView(ssa, showSystemAppsParams);
										((LinearLayout)baseButtonsExpanded[tmpbb]).addView(appList, appListParams);
										((LinearLayout)baseButtonsExpanded[tmpbb]).addView(doneButton, doneButtonParams);
									}

									else

									if(baseButtonsText[tmpbb].equals(NW))
									{
										baseButtonsExpanded[tmpbb] = new LinearLayout(getApplicationContext());
										((LinearLayout)baseButtonsExpanded[tmpbb]).setOrientation(LinearLayout.VERTICAL);
										baseButtonsExpandedParams[tmpbb] = new RelativeLayout.LayoutParams(screenWidth, screenHeight);
										//for future upgradibility
										final int fixesTotal = 4;

										final String[] textsStr = new String[]{
											"Notifications not reflecting in Chat Head? Try restarting NotificafionListener",
											"Try restarting NotificationListener manually from Settings",
											"Check the \'Draw over other apps\' permission",
											"If the app was working previously and suddenly stops, then restarting the device may help"
										};

										final String[] buttonsText = new String[]{
											"Auto Restart",
											"Open Settings",
											"Open Settings",
											""
										};

										final Intent[] bIntents = new Intent[]{
											new Intent(),
											new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
											new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())),
											new Intent()
										};

										ListView fixList = new ListView(getApplicationContext());
										BaseAdapter fixListAdpater = new BaseAdapter(){

											@Override
											public int getCount()
											{
												// TODO: Implement this method
												return fixesTotal;
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
											public View getView(final int i, View view, ViewGroup viewGroup)
											{
												// TODO: Implement this method
												view = new LinearLayout(getApplicationContext());
												((LinearLayout)view).setOrientation(LinearLayout.VERTICAL);

												TextView text = new TextView(getApplicationContext());
												text.setTextColor(Color.LTGRAY);
												text.setText("(" + String.valueOf(i+1) + ")" + "\n" + textsStr[i]);
												//text.setTextSize((int)(xdpi/div)/8);
												LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

												((LinearLayout)view).addView(text, textParams);

												if(!buttonsText[i].equals(""))
												{
													Button button = new Button(getApplicationContext());
													button.setText(buttonsText[i]);
													//button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													button.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View p1)
															{
																// TODO: Implement this method
																if(i == 0)
																{
																	PackageManager pm = getPackageManager();
																	pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotificationScannerService.class),
																								  PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
																	Toast.makeText(getApplicationContext(), "Restarting...", Toast.LENGTH_LONG).show();
																	new CountDownTimer(5000, 500){

																		@Override
																		public void onTick(long p1)
																		{
																			if(!isMyServiceRunning(NotificationScannerService.class))
																			{
																				pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotificationScannerService.class),
																											  PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
																				Toast.makeText(getApplicationContext(), "Restarted", Toast.LENGTH_SHORT).show();
																				cancel();
																			}
																		}

																		@Override
																		public void onFinish()
																		{
																		}
																	}.start();
																	/*pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotificationScannerService.class),
																								  PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
																	Toast.makeText(getApplicationContext(), "Restarted", Toast.LENGTH_SHORT).show();*/
																}else
																	startActivity(bIntents[i]);
															}
														});
													LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													((LinearLayout)view).addView(button, buttonParams);
												}

												return view;
											}
										};
										fixList.setAdapter(fixListAdpater);
										LinearLayout.LayoutParams fixListParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
										((LinearLayout)baseButtonsExpanded[tmpbb]).addView(fixList, fixListParams);
									}
									
									else

									if(baseButtonsText[tmpbb].equals(S))
									{
										baseButtonsExpanded[tmpbb] = new LinearLayout(getApplicationContext());
										((LinearLayout)baseButtonsExpanded[tmpbb]).setOrientation(LinearLayout.VERTICAL);
										baseButtonsExpandedParams[tmpbb] = new RelativeLayout.LayoutParams(screenWidth, screenHeight);

										/*TextView sTitle = new TextView(getApplicationContext());
										 sTitle.setTextColor(Color.WHITE);
										 //sTitle.setTextSize((int)(xdpi/div)/8);
										 sTitle.setText("Settings");

										 LinearLayout.LayoutParams sTitleParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
										 ((LinearLayout)baseButtonsExpanded[tmpbb]).addView(sTitle, sTitleParams);
										 */
										final int settingsTotal = 5;

										final String[] sTexts = new String[]{
											" Chat Head",
											"",
											"",
											" Bot",
											" Notifications"
										};

										final ListView sList = new ListView(getApplicationContext());
										sList.setDivider(new ColorDrawable(Color.parseColor("#008392")));
										sList.setDividerHeight(5);
										BaseAdapter sListAdapter = new BaseAdapter(){

											@Override
											public int getCount()
											{
												// TODO: Implement this method
												return settingsTotal;
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
											public View getView(int i, View view, ViewGroup viewGroup)
											{
												// TODO: Implement this method
												view = new LinearLayout(getApplicationContext());
												((LinearLayout)view).setOrientation(LinearLayout.VERTICAL);

												if(!sTexts[i].equals(""))
												{
													TextView sText = new TextView(getApplicationContext());
													sText.setTextAppearance(android.R.style.TextAppearance_Medium);
													if(i==0)
														sText.setTextAppearance(android.R.style.TextAppearance_Large);
													sText.setTextColor(Color.WHITE);
													sText.setTypeface(Typeface.DEFAULT_BOLD);
													sText.setText(sTexts[i]);
													LinearLayout.LayoutParams sTextParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
													((LinearLayout)view).addView(sText, sTextParams);
												}

												if(i == 0)
												{
													TextView info = new TextView(getApplicationContext());
													info.setTextAppearance(android.R.style.TextAppearance_Medium);
													info.setTextColor(Color.WHITE);
													info.setText(" Size");
													LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													final LinearLayout sViewLayout = new LinearLayout(getApplicationContext());
													sViewLayout.setOrientation(LinearLayout.VERTICAL);

													final ImageView chatHeadImg = new ImageView(getApplicationContext());
													chatHeadImg.setImageResource(R.drawable.ic_logo);
													final LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams((int)(xdpi/chatHeadSizeDiv), (int)(ydpi/chatHeadSizeDiv));

													LinearLayout sizeLayout1 = new LinearLayout(getApplicationContext());
													sizeLayout1.setOrientation(LinearLayout.HORIZONTAL); 
													LinearLayout.LayoutParams sizeLayout1Params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

													final EditText sizeText = new EditText(getApplicationContext());
													sizeText.setTextColor(Color.LTGRAY);
													sizeText.setText(String.valueOf(10f - chatHeadSizeDiv));

													LinearLayout.LayoutParams sizeTextParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
													sizeTextParams.gravity = Gravity.CENTER;

													final ImageView reset = new ImageView(getApplicationContext());
													reset.setImageResource(R.drawable.ic_refresh);
													reset.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View p1)
															{
																// TODO: Implement this method
																try {
																	chatHeadSizeDiv = Float.valueOf(readFromFile(chatHeadSizeDivFile, "SEPARATOR_NEW_LINE")[0]);
																} catch (IOException e) {
																	e.printStackTrace();
																}
																notifyDataSetChanged();
																sList.setAdapter(sList.getAdapter());
															}
														});
													LinearLayout.LayoutParams resetParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
													resetParams.gravity = Gravity.CENTER;

													Button doneButton = new Button(getApplicationContext());
													doneButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													doneButton.setText("Apply");
													doneButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																float val = Float.valueOf(sizeText.getText().subSequence(0, sizeText.getText().length()).toString());
																val = Math.max(1f, val);
																val = Math.min(9f, val);
																chatHeadSizeDiv = 10f - val;
																notifyDataSetChanged();
																sList.setAdapter(sList.getAdapter());
															}
														});

													LinearLayout.LayoutParams doneButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													sizeLayout1.addView(sizeText, sizeTextParams);
													sizeLayout1.addView(reset, resetParams);
													sizeLayout1.addView(doneButton, doneButtonParams);

													final SeekBar sizeBar = new SeekBar(getApplicationContext());
													sizeBar.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
													sizeBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													sizeBar.setMax(10000);
													//sizeBar.setMin(10);
													sizeBar.setProgress(10000 - (int)(chatHeadSizeDiv*1000f));
													sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

															@Override
															public void onProgressChanged(SeekBar seekBar, int progress, boolean p3)
															{
																// TODO: Implement this method
																progress = Math.max(1000, progress);
																progress = Math.min(9000, progress);
																chatHeadSizeDiv = (10000f - (float)sizeBar.getProgress())/1000f;
																sizeText.setText(String.valueOf(10f - chatHeadSizeDiv));
																imgParams.width = (int)(xdpi/chatHeadSizeDiv);
																imgParams.height = (int)(ydpi/chatHeadSizeDiv);
																sViewLayout.updateViewLayout(chatHeadImg, imgParams);
																p3 = true;
															}

															@Override
															public void onStartTrackingTouch(SeekBar p1)
															{
																// TODO: Implement this method
															}

															@Override
															public void onStopTrackingTouch(SeekBar p1)
															{
																// TODO: Implement this method
															}
														});
													LinearLayout.LayoutParams sizeBarParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

													sViewLayout.addView(sizeLayout1, sizeLayout1Params);
													sViewLayout.addView(sizeBar, sizeBarParams);
													sViewLayout.addView(chatHeadImg, imgParams);

													LinearLayout.LayoutParams sViewLayoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
													((LinearLayout)view).addView(info, infoParams);
													((LinearLayout)view).addView(sViewLayout, sViewLayoutParams);
												}

												else

												if(i == 1)
												{
													TextView info = new TextView(getApplicationContext());
													info.setTextAppearance(android.R.style.TextAppearance_Medium);
													info.setTextColor(Color.WHITE);
													info.setText(" Sensitivity");
													LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													final LinearLayout sViewLayout1 = new LinearLayout(getApplicationContext());
													sViewLayout1.setOrientation(LinearLayout.VERTICAL);

													LinearLayout sizeLayout2 = new LinearLayout(getApplicationContext());
													sizeLayout2.setOrientation(LinearLayout.HORIZONTAL); 
													LinearLayout.LayoutParams sizeLayout2Params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

													final EditText sizeText1 = new EditText(getApplicationContext());
													sizeText1.setTextColor(Color.LTGRAY);
													sizeText1.setText(String.valueOf(chatHeadSensitivity));

													LinearLayout.LayoutParams sizeText1Params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
													sizeText1Params.gravity = Gravity.CENTER;

													final ImageView reset1 = new ImageView(getApplicationContext());
													reset1.setImageResource(R.drawable.ic_refresh);
													reset1.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View p1)
															{
																// TODO: Implement this method
																try {
																	chatHeadSensitivity = Integer.parseInt(readFromFile(chatHeadSensitivityFile, "SEPARATOR_NEW_LINE")[0]);
																} catch (IOException e) {
																	e.printStackTrace();
																}
																notifyDataSetChanged();
																sList.setAdapter(sList.getAdapter());
															}
														});
													LinearLayout.LayoutParams reset1Params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
													reset1Params.gravity = Gravity.CENTER;

													Button doneButton1 = new Button(getApplicationContext());
													doneButton1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													doneButton1.setText("Apply");
													doneButton1.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																int val = Integer.parseInt(sizeText1.getText().subSequence(0, sizeText1.getText().length()).toString());
																chatHeadSensitivity = val;
																notifyDataSetChanged();
																sList.setAdapter(sList.getAdapter());
															}
														});

													LinearLayout.LayoutParams doneButton1Params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													sizeLayout2.addView(sizeText1, sizeText1Params);
													sizeLayout2.addView(reset1, reset1Params);
													sizeLayout2.addView(doneButton1, doneButton1Params);

													final SeekBar sizeBar1 = new SeekBar(getApplicationContext());
													sizeBar1.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
													sizeBar1.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													sizeBar1.setMax(50);
													sizeBar1.setMin(10);
													//sizeBar1.setMin(10);
													sizeBar1.setProgress(chatHeadSensitivity);
													sizeBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

															@Override
															public void onProgressChanged(SeekBar seekBar, int progress, boolean p3)
															{
																// TODO: Implement this method
																chatHeadSensitivity = sizeBar1.getProgress();
																sizeText1.setText(String.valueOf(chatHeadSensitivity));
																p3 = true;
															}

															@Override
															public void onStartTrackingTouch(SeekBar p1)
															{
																// TODO: Implement this method
															}

															@Override
															public void onStopTrackingTouch(SeekBar p1)
															{
																// TODO: Implement this method
															}
														});
													LinearLayout.LayoutParams sizeBar1Params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

													sViewLayout1.addView(sizeLayout2, sizeLayout2Params);
													sViewLayout1.addView(sizeBar1, sizeBar1Params);

													LinearLayout.LayoutParams sViewLayout1Params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
													((LinearLayout)view).addView(info, infoParams);
													((LinearLayout)view).addView(sViewLayout1, sViewLayout1Params);
												}

												else

												if(i == 2)
												{
													TextView info = new TextView(getApplicationContext());
													info.setTextAppearance(android.R.style.TextAppearance_Medium);
													info.setTextColor(Color.WHITE);
													info.setText(" Haptic Feedback");
													LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													CheckBox checkbox = new CheckBox(getApplicationContext());
													checkbox.setText("\nEnable Haptics\n");
													checkbox.setTextColor(Color.WHITE);
													checkbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													checkbox.setChecked(hapticsEnabled);
													checkbox.setTextColor(Color.LTGRAY);
													checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

													       @Override
													       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
													    		hapticsEnabled = isChecked;
													       }
													   }
													);

													LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
													((LinearLayout)view).addView(info, infoParams);
													((LinearLayout)view).addView(checkbox, checkboxParams);
												}

												else

												if(i == 3)
												{
													CheckBox checkbox = new CheckBox(getApplicationContext());
													checkbox.setText("\nOTP Bomb Protection\n");
													checkbox.setTextColor(Color.WHITE);
													checkbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													checkbox.setChecked(OTPProtectionEnabled);
													checkbox.setTextColor(Color.LTGRAY);
													checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

													       @Override
													       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
													    		OTPProtectionEnabled = isChecked;
													       }
													   }
													);
													LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													TextView info = new TextView(getApplicationContext());
													info.setTextAppearance(android.R.style.TextAppearance_Medium);
													info.setTextColor(Color.LTGRAY);
													info.setText(" Add or delete saved data");
													LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													Button addButton = new Button(getApplicationContext());
													//addButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													addButton.setText("+Add a Message and a Reply");
													addButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method
																LinearLayout adLayout = new LinearLayout(getApplicationContext());
																adLayout.setOrientation(LinearLayout.VERTICAL);

																final EditText message = new EditText(getApplicationContext());
																message.setHint("Message");
																LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

																final EditText reply = new EditText(getApplicationContext());
																reply.setHint("Reply");
																LinearLayout.LayoutParams replyParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

																adLayout.addView(message, messageParams);
																adLayout.addView(reply, replyParams);

																new AlertDialog.Builder(AppActivity.this).setCancelable(false)
																	.setTitle("Add Bot Data")
																	.setMessage("Add a message(received from Sender) and a reply(to be sent by Bot) below")
																	.setView(adLayout)
																	.setPositiveButton("Add", new DialogInterface.OnClickListener(){

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																			String msg = message.getText().subSequence(0, message.getText().length()).toString();
																			String rly = reply.getText().subSequence(0, reply.getText().length()).toString();

																			bot.learn(msg,rly);
																			
																			refreshAbstracts();

																			Toast.makeText(getApplicationContext(), "Data Added", Toast.LENGTH_SHORT).show();
																		}
																	})
																	.setNegativeButton("Cancel", null)
																	.show();
															}
														});

													LinearLayout.LayoutParams addButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													Button showDataButton = new Button(getApplicationContext());
													//showDataButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													showDataButton.setText("Show Existing Data");
													showDataButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method

																LinearLayout shLayout = new LinearLayout(getApplicationContext());
																shLayout.setOrientation(LinearLayout.VERTICAL);

																final ListView dataList = new ListView(getApplicationContext());
																final BaseAdapter dataListAdapter = new BaseAdapter(){

																	@Override
																	public int getCount()
																	{
																		// TODO: Implement this method
																		return abstracts[REQ].size();
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
																	public View getView(final int i, View view, ViewGroup viewGroup)
																	{
																		// TODO: Implement this method
																		view = new LinearLayout(getApplicationContext());
																		((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL);

																		TextView text = new TextView(getApplicationContext());
																		text.setTextColor(Color.LTGRAY);
																		String reqTxt = "";
																		for(int req = 0;req < ((abstracts[REQ].get(i) != null)? abstracts[REQ].get(i).length : 0);req++)
																			reqTxt += abstracts[REQ].get(i)[req] + ((abstracts[REQ].get(i).length > 1 && req != abstracts[REQ].get(i).length-1)? ", " : "");
																		String resTxt = "";
																		for(int res = 0;res < ((abstracts[RES].get(i) != null)? abstracts[RES].get(i).length : 0);res++)
																			resTxt += abstracts[RES].get(i)[res] + ((abstracts[RES].get(i).length > 1 && res != abstracts[RES].get(i).length-1)? ", " : "");
																		text.setText("(" + String.valueOf(i) + ")" + "\n" + "Message/s : " + reqTxt + "\n\n" + "Reply/s : " + resTxt);
																		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

																		ImageView delete = new ImageView(getApplicationContext());
																		delete.setImageResource(R.drawable.ic_close_round);
																		delete.setOnClickListener(new View.OnClickListener(){

																				@Override
																				public void onClick(View p1)
																				{
																					// TODO: Implement this method
																					abstracts[REQ].set(i, null);
																					abstracts[RES].set(i, null);
																					notifyDataSetChanged();
																				}
																			});
																		LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																		deleteParams.gravity = Gravity.END;

																		((LinearLayout)view).addView(delete, deleteParams);
																		((LinearLayout)view).addView(text, textParams);

																		return view;
																	}
																};
																dataList.setAdapter(dataListAdapter);
																LinearLayout.LayoutParams dataListParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
																shLayout.addView(dataList, dataListParams);

																new AlertDialog.Builder(AppActivity.this)
																	.setTitle("Bot Data(User Defined / Default)")
																	.setView(shLayout)
																	.setPositiveButton("Done", null)
																	.show();
															}
														});
													LinearLayout.LayoutParams showDataButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);


													((LinearLayout)view).addView(checkbox, checkboxParams);
													((LinearLayout)view).addView(info, infoParams);
													((LinearLayout)view).addView(showDataButton, showDataButtonParams);
													((LinearLayout)view).addView(addButton, addButtonParams);
												}

												/*else

												if(i == 3)
												{
													TextView asToInfo = new TextView(getApplicationContext());
													asToInfo.setTextColor(Color.LTGRAY);
													asToInfo.setText("Auto Reply to Users/Senders : ");
													LinearLayout.LayoutParams asToInfoParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													Button showAsToButton = new Button(getApplicationContext());
													showAsToButton.setText("Show existing Senders");
													LinearLayout.LayoutParams showAsToButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													final ListView asToList = new ListView(getApplicationContext());
													final BaseAdapter asToListAdapter = new BaseAdapter(){

														@Override
														public int getCount()
														{
															// TODO: Implement this method
															return autoSendTos.size() + autoSendTosU.size();
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
														public View getView(final int i, View view, ViewGroup viewGroup)
														{
															// TODO: Implement this method

															view = new LinearLayout(getApplicationContext());
															((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL);

															TextView asTxt = new TextView(getApplicationContext());
															//asTxt.setTextSize((int)(xdpi/div)/8);
															if(i < autoSendTos.size())
															{
																asTxt.setTextColor(Color.GREEN);
																asTxt.setText(autoSendTos.get(i));
															}
															else
															{
																asTxt.setTextColor(Color.WHITE);
																asTxt.setText(autoSendTosU.get(i - autoSendTos.size()) + "(User Defined)");
															}

															LinearLayout.LayoutParams asTxtParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

															ImageView delete = new ImageView(getApplicationContext());
															delete.setImageResource(R.drawable.ic_close_round);
															delete.setOnClickListener(new View.OnClickListener(){

																	@Override
																	public void onClick(View p1)
																	{
																		// TODO: Implement this method
																		if(i < autoSendTos.size())
																		{
																			autoSendTos.remove(i);
																			autoSendTosFile.delete();
																			try {
																				autoSendTosFile.createNewFile();
																				writeToFile(autoSendTosFile, autoSendTos.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
																			} catch (IOException e) {
																				e.printStackTrace();
																			}
																		}
																		else
																		{
																			autoSendTosU.remove(i - autoSendTos.size());
																			try {
																				autoSendTosUFile.delete();
																				autoSendTosUFile.createNewFile();
																				writeToFile(autoSendTosUFile, autoSendTosU.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
																			} catch (IOException e) {
																				e.printStackTrace();
																			}
																		}
																		notifyDataSetChanged();
																	}
																});
															LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
															deleteParams.gravity = Gravity.END;

															((LinearLayout)view).addView(delete, deleteParams);
															((LinearLayout)view).addView(asTxt, asTxtParams);

															view.setOnLongClickListener(new View.OnLongClickListener(){

																	@Override
																	public boolean onLongClick(View view)
																	{
																		// TODO: Implement this method
																		new AlertDialog.Builder(AppActivity.this).setCancelable(false)
																			.setTitle("Delete")
																			.setMessage("Delete user/sender from autoSends list?")
																			.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

																				@Override
																				public void onClick(DialogInterface p1, int p2)
																				{
																					// TODO: Implement this method
																					if(i < autoSendTos.size())
																					{
																						autoSendTos.remove(i);
																						try {
																							autoSendTosFile.delete();
																							autoSendTosFile.createNewFile();
																							writeToFile(autoSendTosFile, autoSendTos.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
																						} catch (IOException e) {
																							e.printStackTrace();
																						}
																					}
																					else {
																						autoSendTosU.remove(i - autoSendTos.size());
																						try {
																							autoSendTosUFile.delete();
																							autoSendTosUFile.createNewFile();
																							writeToFile(autoSendTosUFile, autoSendTosU.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
																						}catch (IOException e) {
																							e.printStackTrace();
																						}
																					}
																					notifyDataSetChanged();
																				}
																			})
																			.setNegativeButton("No", null)
																			.show();
																		return false;
																	}
																});

															return view;
														}
													};
													asToList.setAdapter(asToListAdapter);

													showAsToButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method

																final LinearLayout asToListLayout = new LinearLayout(getApplicationContext());
																LinearLayout.LayoutParams asToListParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																asToListLayout.addView(asToList, asToListParams);

																new AlertDialog.Builder(AppActivity.this).setCancelable(false)
																	.setTitle("Auto Reply Users/Senders")
																	.setMessage("")
																	.setView(asToListLayout)
																	.setPositiveButton("Done", new DialogInterface.OnClickListener(){

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																			// TODO: Implement this method
																			asToListLayout.removeView(asToList);
																		}
																	})
																	.show();
															}
														});

													Button addAsToButton = new Button(getApplicationContext());
													addAsToButton.setText("+Add User/Sender");
													LinearLayout.LayoutParams addAsToButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													addAsToButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method

																final LinearLayout addAsToTxtLayout = new LinearLayout(getApplicationContext());
																final EditText addAsToTxt = new EditText(getApplicationContext());
																final LinearLayout.LayoutParams addAsToTxtParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
																addAsToTxtLayout.addView(addAsToTxt, addAsToTxtParams);

																new AlertDialog.Builder(AppActivity.this).setCancelable(false)
																	.setTitle("Add User/Sender")
																	.setMessage("")
																	.setView(addAsToTxtLayout)
																	.setPositiveButton("Add", new DialogInterface.OnClickListener(){

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																			String txt = addAsToTxt.getText().subSequence(0, addAsToTxt.getText().length()).toString();
																			if(!txt.trim().equals(""))
																			{
																				try{
																					writeToFile(autoSendTosUFile, new String[]{txt}, "SEPARATOR_NEW_LINE");
																				}catch (IOException e) {
																					e.printStackTrace();
																				}
																				autoSendTosU.add(txt);

																				Toast.makeText(getApplicationContext(), "user/sender added", Toast.LENGTH_SHORT).show();
																			}
																		}
																	})
																	.setNegativeButton("Cancel", null)
																	.show();
															}
														});

													((LinearLayout)view).addView(asToInfo, asToInfoParams);
													((LinearLayout)view).addView(showAsToButton, showAsToButtonParams);
													((LinearLayout)view).addView(addAsToButton, addAsToButtonParams);

													TextView asMsgInfo = new TextView(getApplicationContext());
													asMsgInfo.setTextColor(Color.LTGRAY);
													asMsgInfo.setText("Auto Reply to Messages : ");
													LinearLayout.LayoutParams asMsgInfoParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													Button showAsMsgButton = new Button(getApplicationContext());
													showAsMsgButton.setText("Show existing Messages");
													LinearLayout.LayoutParams showAsMsgButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													final ListView asMsgList = new ListView(getApplicationContext());
													final BaseAdapter asMsgListAdapter = new BaseAdapter(){

														@Override
														public int getCount()
														{
															// TODO: Implement this method
															return autoSendMsgs.size() + autoSendMsgsU.size();
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
														public View getView(final int i, View view, ViewGroup viewGroup)
														{
															// TODO: Implement this method
															if(i < autoSendMsgs.size() + autoSendMsgsU.size())
															{
																view = new LinearLayout(getApplicationContext());
																((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL);

																TextView asTxt = new TextView(getApplicationContext());
																//asTxt.setTextSize((int)(xdpi/div)/8);
																if(i < autoSendMsgs.size())
																{
																	asTxt.setTextColor(Color.GREEN);
																	asTxt.setText(autoSendMsgs.get(i));
																}
																else
																{
																	asTxt.setTextColor(Color.WHITE);
																	asTxt.setText(autoSendMsgsU.get(i - autoSendMsgs.size()) + "(User Defined)");
																}

																LinearLayout.LayoutParams asTxtParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

																ImageView delete = new ImageView(getApplicationContext());
																delete.setImageResource(R.drawable.ic_close_round);
																delete.setOnClickListener(new View.OnClickListener(){

																		@Override
																		public void onClick(View p1)
																		{
																			// TODO: Implement this method
																			if(i < autoSendMsgs.size())
																			{
																				autoSendMsgs.remove(i);
																				try {
																					autoSendMsgsFile.delete();
																					autoSendMsgsFile.createNewFile();
																					writeToFile(autoSendMsgsFile, autoSendMsgs.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
																				}catch (IOException e) {
																					e.printStackTrace();
																				}
																			}
																			else
																			{
																				autoSendMsgsU.remove(i - autoSendMsgs.size());
																				try {
																					autoSendMsgsUFile.delete();
																					autoSendMsgsUFile.createNewFile();
																					writeToFile(autoSendMsgsUFile, autoSendMsgsU.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
																				}catch (IOException e) {
																					e.printStackTrace();
																				}
																			}
																			notifyDataSetChanged();
																		}
																	});
																LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																deleteParams.gravity = Gravity.END;

																((LinearLayout)view).addView(delete, deleteParams);
																((LinearLayout)view).addView(asTxt, asTxtParams);

																view.setOnLongClickListener(new View.OnLongClickListener(){

																		@Override
																		public boolean onLongClick(View view)
																		{
																			// TODO: Implement this method
																			new AlertDialog.Builder(AppActivity.this).setCancelable(false)
																				.setTitle("Delete")
																				.setMessage("Delete message from autoSends list?")
																				.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

																					@Override
																					public void onClick(DialogInterface p1, int p2)
																					{
																						// TODO: Implement this method
																						if(i < autoSendMsgs.size())
																						{
																							autoSendMsgs.remove(i);
																							try {
																								autoSendMsgsFile.delete();
																								autoSendMsgsFile.createNewFile();
																								writeToFile(autoSendMsgsFile, autoSendMsgs.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
																							}catch (IOException e) {
																								e.printStackTrace();
																							}
																						}
																						else {
																							autoSendMsgsU.remove(i - autoSendMsgs.size());
																							try {
																								autoSendMsgsUFile.delete();
																								autoSendMsgsUFile.createNewFile();
																								writeToFile(autoSendMsgsUFile, autoSendMsgsU.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
																							}catch (IOException e) {
																								e.printStackTrace();
																							}
																						}
																						notifyDataSetChanged();
																					}
																				})
																				.setNegativeButton("No", null)
																				.show();
																			return false;
																		}
																	});
															}

															return view;
														}
													};
													asMsgList.setAdapter(asMsgListAdapter);

													showAsMsgButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method

																final LinearLayout asMsgListLayout = new LinearLayout(getApplicationContext());
																LinearLayout.LayoutParams asMsgListParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																asMsgListLayout.addView(asMsgList, asMsgListParams);

																new AlertDialog.Builder(AppActivity.this).setCancelable(false)
																	.setTitle("Auto Reply Messages")
																	.setMessage("")
																	.setView(asMsgListLayout)
																	.setPositiveButton("Done", new DialogInterface.OnClickListener(){

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																			// TODO: Implement this method
																			asMsgListLayout.removeView(asMsgList);
																		}
																	})
																	.show();
															}
														});

													Button addAsMsgButton = new Button(getApplicationContext());
													addAsMsgButton.setText("+Add Message");
													LinearLayout.LayoutParams addAsMsgButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													addAsMsgButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method

																final LinearLayout addAsMsgTxtLayout = new LinearLayout(getApplicationContext());
																final EditText addAsMsgTxt = new EditText(getApplicationContext());
																final LinearLayout.LayoutParams addAsMsgTxtParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
																addAsMsgTxtLayout.addView(addAsMsgTxt, addAsMsgTxtParams);

																new AlertDialog.Builder(AppActivity.this).setCancelable(false)
																	.setTitle("Add Message")
																	.setMessage("")
																	.setView(addAsMsgTxtLayout)
																	.setPositiveButton("Add", new DialogInterface.OnClickListener(){

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																			String txt = addAsMsgTxt.getText().subSequence(0, addAsMsgTxt.getText().length()).toString();
																			if(!txt.trim().equals(""))
																			{
																				try{
																					writeToFile(autoSendMsgsUFile, new String[]{txt}, "SEPARATOR_NEW_LINE");
																				}catch (IOException e) {
																					e.printStackTrace();
																				}
																				autoSendMsgsU.add(txt);

																				Toast.makeText(getApplicationContext(), "message added", Toast.LENGTH_SHORT).show();
																			}
																		}
																	})
																	.setNegativeButton("Cancel", null)
																	.show();
															}
														});

													((LinearLayout)view).addView(asMsgInfo, asMsgInfoParams);
													((LinearLayout)view).addView(showAsMsgButton, showAsMsgButtonParams);
													((LinearLayout)view).addView(addAsMsgButton, addAsMsgButtonParams);
												}*/

												else

												if(i == 4)
												{
													CheckBox checkbox = new CheckBox(getApplicationContext());
													checkbox.setText("\nKeep Notifications\n");
													checkbox.setTextColor(Color.WHITE);
													checkbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));//setButtonTintList is accessible directly on API>19
													checkbox.setChecked(keepNotifications);
													checkbox.setTextColor(Color.LTGRAY);
													checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

													       @Override
													       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
													    		keepNotifications = isChecked;
													       }
													   }
													);

													LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
													((LinearLayout)view).addView(checkbox, checkboxParams);

													TextView igTitle = new TextView(getApplicationContext());
													igTitle.setTextAppearance(android.R.style.TextAppearance_Medium);
													igTitle.setTextColor(Color.LTGRAY);
													igTitle.setText(" Ignore Notifications with Titles : ");
													LinearLayout.LayoutParams igTitleParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													Button showTitlesButton = new Button(getApplicationContext());
													//showTitlesButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													showTitlesButton.setText("Show Existing Titles");
													showTitlesButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method
																LinearLayout shLayout = new LinearLayout(getApplicationContext());
																shLayout.setOrientation(LinearLayout.VERTICAL);

																final ListView titles = new ListView(getApplicationContext());
																final BaseAdapter titlesAdapter = new BaseAdapter(){

																	@Override
																	public int getCount()
																	{
																		// TODO: Implement this method
																		return ignoreTitles.size();
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
																	public View getView(final int i, View view, ViewGroup viewGroup)
																	{
																		// TODO: Implement this method
																		view = new LinearLayout(getApplicationContext());
																		((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL);

																		TextView title = new TextView(getApplicationContext());
																		title.setTextColor(Color.LTGRAY);
																		title.setText(ignoreTitles.get(i));
																		LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

																		ImageView delete = new ImageView(getApplicationContext());
																		delete.setImageResource(R.drawable.ic_close_round);
																		delete.setOnClickListener(new View.OnClickListener(){

																				@Override
																				public void onClick(View p1)
																				{
																					// TODO: Implement this method
																					ignoreTitles.remove(i);
																					notifyDataSetChanged();
																				}
																			});
																		LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																		deleteParams.gravity = Gravity.END;

																		((LinearLayout)view).addView(delete, deleteParams);
																		((LinearLayout)view).addView(title, titleParams);

																		return view;
																	}
																};
																titles.setAdapter(titlesAdapter);
																LinearLayout.LayoutParams titlesParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
																shLayout.addView(titles, titlesParams);

																new AlertDialog.Builder(AppActivity.this)
																	.setTitle("Ignore Titles")
																	.setView(shLayout)
																	.setPositiveButton("Done", null)
																	.show();
															}
														});
													LinearLayout.LayoutParams showTitlesButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													Button addTitleButton = new Button(getApplicationContext());
													//addTitleButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													addTitleButton.setText("+Add Title");
													addTitleButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method
																LinearLayout adLayout = new LinearLayout(getApplicationContext());
																adLayout.setOrientation(LinearLayout.VERTICAL);

																final EditText title = new EditText(getApplicationContext());
																title.setHint("Title");
																LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

																adLayout.addView(title, titleParams);

																new AlertDialog.Builder(AppActivity.this).setCancelable(false)
																	.setTitle("Add Ignore Title")
																	.setView(adLayout)
																	.setPositiveButton("Add", new DialogInterface.OnClickListener(){

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																			String ttl = title.getText().subSequence(0, title.getText().length()).toString();
																			if(!ttl.trim().equals(""))
																			{
																				ignoreTitles.add(ttl);
																				Toast.makeText(getApplicationContext(), "Title Added", Toast.LENGTH_SHORT).show();
																			}
																		}
																	})
																	.setNegativeButton("Cancel", null)
																	.show();
															}
														});
													LinearLayout.LayoutParams addButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													((LinearLayout)view).addView(igTitle, igTitleParams);
													((LinearLayout)view).addView(showTitlesButton, showTitlesButtonParams);
													((LinearLayout)view).addView(addTitleButton, addButtonParams);

													TextView igText = new TextView(getApplicationContext());
													igText.setTextAppearance(android.R.style.TextAppearance_Medium);
													igText.setTextColor(Color.LTGRAY);
													igText.setText(" Ignore Notifications with Texts : ");
													LinearLayout.LayoutParams igTextParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													Button showTextsButton = new Button(getApplicationContext());
													//showTextsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													showTextsButton.setText("Show Existing Texts");
													showTextsButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method
																LinearLayout shLayout = new LinearLayout(getApplicationContext());
																shLayout.setOrientation(LinearLayout.VERTICAL);

																final ListView texts = new ListView(getApplicationContext());
																final BaseAdapter textsAdapter = new BaseAdapter(){

																	@Override
																	public int getCount()
																	{
																		// TODO: Implement this method
																		return ignoreTexts.size();
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
																	public View getView(final int i, View view, ViewGroup viewGroup)
																	{
																		// TODO: Implement this method
																		view = new LinearLayout(getApplicationContext());
																		((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL);

																		TextView text = new TextView(getApplicationContext());
																		text.setTextColor(Color.LTGRAY);
																		text.setText(ignoreTexts.get(i));
																		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

																		ImageView delete = new ImageView(getApplicationContext());
																		delete.setImageResource(R.drawable.ic_close_round);
																		delete.setOnClickListener(new View.OnClickListener(){

																				@Override
																				public void onClick(View p1)
																				{
																					// TODO: Implement this method
																					ignoreTexts.remove(i);
																					notifyDataSetChanged();
																				}
																			});
																		LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																		deleteParams.gravity = Gravity.END;

																		((LinearLayout)view).addView(delete, deleteParams);
																		((LinearLayout)view).addView(text, textParams);

																		return view;
																	}
																};
																texts.setAdapter(textsAdapter);
																LinearLayout.LayoutParams textsParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
																shLayout.addView(texts, textsParams);

																new AlertDialog.Builder(AppActivity.this)
																	.setTitle("Ignore Texts")
																	.setView(shLayout)
																	.setPositiveButton("Done", null)
																	.show();
															}
														});
													LinearLayout.LayoutParams showTextsButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													Button addTextButton = new Button(getApplicationContext());
													//addTextButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
													addTextButton.setText("+Add Ignore Text");
													addTextButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method
																LinearLayout adLayout = new LinearLayout(getApplicationContext());
																adLayout.setOrientation(LinearLayout.VERTICAL);

																final EditText text = new EditText(getApplicationContext());
																text.setHint("Text");
																LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

																adLayout.addView(text, textParams);

																new AlertDialog.Builder(AppActivity.this).setCancelable(false)
																	.setTitle("Add Text")
																	.setView(adLayout)
																	.setPositiveButton("Add", new DialogInterface.OnClickListener(){

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																			String txt = text.getText().subSequence(0, text.getText().length()).toString();
																			if(!txt.trim().equals(""))
																			{
																				ignoreTexts.add(txt);
																				Toast.makeText(getApplicationContext(), "Text Added", Toast.LENGTH_SHORT).show();
																			}
																		}
																	})
																	.setNegativeButton("Cancel", null)
																	.show();
															}
														});
													LinearLayout.LayoutParams addTextButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													((LinearLayout)view).addView(igText, igTextParams);
													((LinearLayout)view).addView(showTextsButton, showTextsButtonParams);
													((LinearLayout)view).addView(addTextButton, addTextButtonParams);
												}

												/*else

												if(i == 3)
												{
													Button showSentsButton = new Button(getApplicationContext());
													showSentsButton.setText("Show Send History");
													showSentsButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method
																LinearLayout shLayout = new LinearLayout(getApplicationContext());
																shLayout.setOrientation(LinearLayout.VERTICAL);

																final ListView sentsList = new ListView(getApplicationContext());
																final BaseAdapter sentsListAdapter = new BaseAdapter(){

																	@Override
																	public int getCount()
																	{
																		// TODO: Implement this method
																		return sents.size();
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
																	public View getView(final int i, View view, ViewGroup viewGroup)
																	{
																		// TODO: Implement this method
																		view = new LinearLayout(getApplicationContext());
																		((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL);

																		TextView sentName = new TextView(getApplicationContext());
																		sentName.setTextColor(Color.LTGRAY);
																		sentName.setText(sents.get(i));
																		LinearLayout.LayoutParams sentNameParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

																		ImageView delete = new ImageView(getApplicationContext());
																		delete.setImageResource(R.drawable.ic_close_round);
																		delete.setOnClickListener(new View.OnClickListener(){

																				@Override
																				public void onClick(View p1)
																				{
																					// TODO: Implement this method
																					sents.remove(i);
																					notifyDataSetChanged();
																				}
																			});
																		LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																		deleteParams.gravity = Gravity.END;

																		((LinearLayout)view).addView(delete, deleteParams);
																		((LinearLayout)view).addView(sentName, sentNameParams);

																		return view;
																	}
																};
																sentsList.setAdapter(sentsListAdapter);
																LinearLayout.LayoutParams sentsListParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
																shLayout.addView(sentsList, sentsListParams);

																new AlertDialog.Builder(AppActivity.this)
																	.setTitle("Send History")
																	.setView(shLayout)
																	.setPositiveButton("Done", null)
																	.setNegativeButton("Clear History", new DialogInterface.OnClickListener(){

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																			// TODO: Implement this method
																			sents = new ArrayList<String>();
																		}
																	})
																	.show();
															}
														});
													LinearLayout.LayoutParams showSentsButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													Button showIgnoredsButton = new Button(getApplicationContext());
													showIgnoredsButton.setText("Show Ignore History");
													showIgnoredsButton.setOnClickListener(new View.OnClickListener(){

															@Override
															public void onClick(View view)
															{
																// TODO: Implement this method
																LinearLayout shLayout = new LinearLayout(getApplicationContext());
																shLayout.setOrientation(LinearLayout.VERTICAL);

																final ListView ignoredsList = new ListView(getApplicationContext());
																final BaseAdapter ignoredsListAdapter = new BaseAdapter(){

																	@Override
																	public int getCount()
																	{
																		// TODO: Implement this method
																		return ignoreds.size();
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
																	public View getView(final int i, View view, ViewGroup viewGroup)
																	{
																		// TODO: Implement this method
																		view = new LinearLayout(getApplicationContext());
																		((LinearLayout)view).setOrientation(LinearLayout.HORIZONTAL);

																		TextView ignoredName = new TextView(getApplicationContext());
																		ignoredName.setTextColor(Color.LTGRAY);
																		ignoredName.setText(ignoreds.get(i));
																		LinearLayout.LayoutParams ignoredNameParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

																		ImageView delete = new ImageView(getApplicationContext());
																		delete.setImageResource(R.drawable.ic_close_round);
																		delete.setOnClickListener(new View.OnClickListener(){

																				@Override
																				public void onClick(View p1)
																				{
																					// TODO: Implement this method
																					ignoreds.remove(i);
																					notifyDataSetChanged();
																				}
																			});
																		LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
																		deleteParams.gravity = Gravity.END;

																		((LinearLayout)view).addView(delete, deleteParams);
																		((LinearLayout)view).addView(ignoredName, ignoredNameParams);

																		return view;
																	}
																};
																ignoredsList.setAdapter(ignoredsListAdapter);
																LinearLayout.LayoutParams ignoredsListParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
																shLayout.addView(ignoredsList, ignoredsListParams);

																new AlertDialog.Builder(AppActivity.this)
																	.setTitle("Ignore History")
																	.setView(shLayout)
																	.setPositiveButton("Done", null)
																	.setNegativeButton("Clear History", new DialogInterface.OnClickListener(){

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																			// TODO: Implement this method
																			ignoreds = new ArrayList<String>();
																		}
																	})
																	.show();
															}
														});
													LinearLayout.LayoutParams showIgnoredsButtonParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

													((LinearLayout)view).addView(showSentsButton, showSentsButtonParams);
													((LinearLayout)view).addView(showIgnoredsButton, showIgnoredsButtonParams);
												}*/

												return view;
											}
										};
										sList.setAdapter(sListAdapter);
										LinearLayout.LayoutParams sListParams = new LinearLayout.LayoutParams(MATCH_PARENT, screenHeight - Math.min(baseButtonsWidth, baseButtonsHeight)/2);

										Button saveButton = new Button(getApplicationContext());
										saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00dff9")));
										saveButton.setText("Save");
										saveButton.setOnClickListener(new View.OnClickListener() {

											@Override
											public void onClick(View p1) {
												// TODO: Implement this method
												try {
													if (chatHeadSizeDiv != Float.valueOf(readFromFile(chatHeadSizeDivFile, "SEPARATOR_NEW_LINE")[0])) {
														chatHeadSizeDivFile.delete();
														chatHeadSizeDivFile.createNewFile();
														writeToFile(chatHeadSizeDivFile, new String[]{String.valueOf(chatHeadSizeDiv)}, "SEPARATOR_NEW_LINE");
													}
													if (chatHeadSensitivity != Integer.parseInt(readFromFile(chatHeadSensitivityFile, "SEPARATOR_NEW_LINE")[0])) {
														chatHeadSensitivityFile.delete();
														chatHeadSensitivityFile.createNewFile();
														writeToFile(chatHeadSensitivityFile, new String[]{String.valueOf(chatHeadSensitivity)}, "SEPARATOR_NEW_LINE");
													}
													if (hapticsEnabled != Boolean.parseBoolean(readFromFile(hapticsEnabledFile, "SEPARATOR_NEW_LINE")[0])) {
														hapticsEnabledFile.delete();
														hapticsEnabledFile.createNewFile();
														writeToFile(hapticsEnabledFile, new String[]{String.valueOf(hapticsEnabled)}, "SEPARATOR_NEW_LINE");
													}
													if (OTPProtectionEnabled != Boolean.parseBoolean(readFromFile(OTPProtectionEnabledFile, "SEPARATOR_NEW_LINE")[0])) {
														OTPProtectionEnabledFile.delete();
														OTPProtectionEnabledFile.createNewFile();
														writeToFile(OTPProtectionEnabledFile, new String[]{String.valueOf(OTPProtectionEnabled)}, "SEPARATOR_NEW_LINE");

														int chatHeadSize = (int)(xdpi / 2.3f);
														LinearLayout initDialogLayout = new LinearLayout(getApplicationContext());
														initDialogLayout.setOrientation(LinearLayout.HORIZONTAL);
														final ImageView refreshView = new ImageView(getApplicationContext());
														refreshView.setImageResource(R.drawable.ic_refresh);
														refreshView.setScaleX(-1);
														LinearLayout.LayoutParams refreshViewParams = new LinearLayout.LayoutParams(chatHeadSize/2+chatHeadSize/2+chatHeadSize/8, chatHeadSize/2);
														refreshViewParams.setMargins(chatHeadSize/2, 0, chatHeadSize/8, 0);
														initDialogLayout.addView(refreshView, refreshViewParams);
														final CountDownTimer refreshLoadingTimer = new CountDownTimer(360*2, 1){

															@Override
															public void onTick(long p1)
															{
																refreshView.setRotation(360-p1/2);
															}

															@Override
															public void onFinish()
															{
																refreshView.setRotation(0);
																start();
															}
														};
														final TextView message = new TextView(getApplicationContext());
														message.setText("This will take some time");
														message.setTextColor(Color.LTGRAY);
														message.setGravity(Gravity.CENTER_VERTICAL);
														initDialogLayout.addView(message, new LinearLayout.LayoutParams(MATCH_PARENT, chatHeadSize/2));
														final AlertDialog initDialog = new AlertDialog.Builder(AppActivity.this).setCancelable(false).setTitle("Retraining").setView(initDialogLayout).create();
														initDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
														initDialog.setOnShowListener(new DialogInterface.OnShowListener() {
												            @Override
												            public void onShow(DialogInterface arg0) {
												                int titleId = getResources().getIdentifier("alertTitle", "id", "android");
												                TextView title = (TextView) initDialog.findViewById(titleId);
																title.setTextColor(Color.parseColor("#01def9"));
												            }
														});
														new Thread(){
												            public void run(){
												            	try
																{
																	runOnUiThread(new Runnable() {
																	    @Override
																	    public void run() {
																	        initDialog.show();
																	        refreshLoadingTimer.start();
																	    }
																	});
																	Training trainModule = new Training(SpamDir.getAbsolutePath());
																	trainModule.preProcessFiles(OTPProtectionEnabled? new String[]{"data"} : new String[]{"data", "otp"});
																}
																catch(Exception e)
																{}finally{
																	runOnUiThread(new Runnable() {
																	    @Override
																	    public void run() {
																	    	refreshLoadingTimer.cancel();
																	        initDialog.dismiss();
																	        Intent intent = getIntent();
																			finish();
																			startActivity(intent);
																	    }
																	});
																}
												            }
												        }.start();
													}
													if (keepNotifications != Boolean.parseBoolean(readFromFile(keepNotificationsFile, "SEPARATOR_NEW_LINE")[0])) {
														keepNotificationsFile.delete();
														keepNotificationsFile.createNewFile();
														writeToFile(keepNotificationsFile, new String[]{String.valueOf(keepNotifications)}, "SEPARATOR_NEW_LINE");
													}

													boolean updateNL = false;
													if (!ignoreTitles.toArray(new String[]{}).equals(readFromFile(ignoreTitlesFile, "SEPARATOR_NEW_LINE"))) {
														ignoreTitlesFile.delete();
														ignoreTitlesFile.createNewFile();
														writeToFile(ignoreTitlesFile, ignoreTitles.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
														updateNL = true;
													}
												if (!ignoreTexts.toArray(new String[]

														{
														}).

														equals(readFromFile(ignoreTextsFile, "SEPARATOR_NEW_LINE"))) {
													ignoreTextsFile.delete();
													ignoreTextsFile.createNewFile();
													writeToFile(ignoreTextsFile, ignoreTexts.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
													updateNL = true;
												}

												if (!sents.toArray(new String[]

																{
																}).

														equals(readFromFile(sentsFile, "SEPARATOR_NEW_LINE"))) {
													sentsFile.delete();
													sentsFile.createNewFile();
													writeToFile(sentsFile, sents.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
												}

												if (!ignoreds.toArray(new String[]

														{
														}).

														equals(readFromFile(ignoredsFile, "SEPARATOR_NEW_LINE"))) {
													ignoredsFile.delete();
													ignoredsFile.createNewFile();
													writeToFile(ignoredsFile, ignoreds.toArray(new String[]{}), "SEPARATOR_NEW_LINE");
												}

												for (
														int req = 0;
														req < abstracts[REQ].

																size();

														req++) {
													if (abstracts[REQ].get(req) == null) {
														File file = new File(absDir, abstractsFileName[REQ].get(req));
														if (file.exists())
															file.delete();
													}
												}

												for (
														int req = 0;
														req < abstracts[REQ].

																size();

														req++) {
													if (abstracts[RES].get(req) == null) {
														File file = new File(absDir, abstractsFileName[RES].get(req));
														if (file.exists())
															file.delete();
													}
												}

												if (updateNL) {
													Intent NLIntent = new Intent(AppActivity.this, NotificationScannerService.class);
													NLIntent.putExtra("type", "update");
													startService(NLIntent);
												}}catch (IOException e) {
																			  e.printStackTrace();
																		  }
													closeButton.performClick();
													Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
												}
											});
										LinearLayout.LayoutParams saveButtonParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);

										((LinearLayout)baseButtonsExpanded[tmpbb]).addView(sList, sListParams);
										((LinearLayout)baseButtonsExpanded[tmpbb]).addView(saveButton, saveButtonParams);
									}

									else
									{
										baseButtonsExpanded[tmpbb] = new View(getApplicationContext());
										baseButtonsExpandedParams[tmpbb] = new RelativeLayout.LayoutParams(screenWidth, screenHeight);
									}

									baseButtonsExpanded[tmpbb].setVisibility(View.GONE);
									baseLayout.addView(baseButtonsExpanded[tmpbb], baseButtonsExpandedParams[tmpbb]);
									//baseLayout.addView(closeButton, closeButtonParams);
									baseButtons[tmpbb].setVisibility(View.GONE);
									baseButtonsExpanded[tmpbb].setVisibility(View.VISIBLE);
								}
								baseButtonTransTimerFinished = true;
							}
						};

						@Override
						public void onClick(View p1)
						{
							// TODO: Implement this method
							if(baseButtonTransTimerFinished && baseButtons[tmpbb].getVisibility() == View.VISIBLE)
							{
								if(baseButtonsExpandable[tmpbb])
								{
									for(int bb = 0;bb < baseButtonsTotal;bb++)
									{
										if(bb != tmpbb)
											baseButtons[bb].setVisibility(View.GONE);
									}

									////////////////////////////////////////////
									closeButton = new ImageView(getApplicationContext());
									closeButton.setImageResource(R.drawable.ic_close);
									closeButton.setAlpha(0.5f);
									closeButton.setOnClickListener(new View.OnClickListener(){

											@Override
											public void onClick(View view)
											{
												// TODO: Implement this method
												if(baseButtonTransTimerFinished)
												{
													reverse = true;
													baseButtonTransTimer.start();

													for(int rbb = 0;rbb < baseButtonsTotal;rbb++)
														baseButtons[rbb].setVisibility(View.VISIBLE);

													baseLayout.removeView(closeButton);
													baseButtonsExpanded[tmpbb].setVisibility(View.GONE);

													baseLayout.setOnClickListener(null);
												}
											}
										});
									closeButtonParams = new RelativeLayout.LayoutParams(chatHeadSize, chatHeadSize);
									closeButtonParams.setMargins(screenWidth - chatHeadSize,
																 0,
																 0,
																 screenHeight - chatHeadSize);
									/////////////////////////////////////////////

									reverse = false;
									baseButtonTransTimer.start();
								}
								else
								{
									if(baseButtonsText[tmpbb].equals(CCH))
									{
										/*if(!chsCreated)
										 {*/
										Intent creatChatHeadIntent = new Intent(AppActivity.this, ChatHeadService.class)
											.putExtra("type", "init");
										startService(creatChatHeadIntent);

										refreshAbstracts();
										int[] absIndices = new int[]{
											random.nextInt(abstracts[REQ].size()),
											0
										};

										absIndices[1] = random.nextInt(abstracts[REQ].get(absIndices[0]).length);

										Intent updateChatHeadIntent = new Intent(AppActivity.this, ChatHeadService.class)
											.putExtra("type", "update")
											.putExtra("data", new String[][]{{"Bot"}, {abstracts[REQ].get(absIndices[0])[absIndices[1]]}})
											.putExtra("icon", Icon.createWithResource(AppActivity.this, R.drawable.ic_logo_small))
											.putExtra("id", String.valueOf(random.nextInt()))
											//.putExtra("channel", "channel")
											.putExtra("bIcon", (Icon)null)
											.putExtra("image", (Bitmap)null)
											.putExtra("pkg", getPackageName());
										try
										{startService(updateChatHeadIntent);}catch(Exception e)
										{}//{Toast.makeText(getApplicationContext(), e.toString()+"\n\n"+e.getStackTrace()[0].toString(), Toast.LENGTH_LONG).show();}

										chsCreated = true;
										/*}
										 else
										 Toast.makeText(getApplicationContext(), "already created", Toast.LENGTH_SHORT).show();*/
									}
									
									else

									if(baseButtonsText[tmpbb].equals(Su))
									{
										Intent intent = new Intent(Intent.ACTION_SENDTO);
										intent.setData(Uri.parse("mailto:"));
										intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "prajwal8074@gmail.com" });
										intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding HoloChat");

										startActivity(Intent.createChooser(intent, "Email via..."));
									}

									else

									if(baseButtonsText[tmpbb].equals(TUT))
									{
										Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://holochat.pro"));
										startActivity(openUrlIntent);
									}
								}
							}
						}
					});

				baseButtonsParams[bb] = new RelativeLayout.LayoutParams(baseButtonsWidth, baseButtonsHeight);
				baseButtonsParams[bb].setMargins(((bb%columnsTotal)) * baseButtonsWidth,
												 (bb%columnsTotal == 0? bb/columnsTotal : (bb-(bb%columnsTotal))/columnsTotal) * baseButtonsHeight,
												 0,
												 0);
				baseLayout.addView(baseButtons[bb], baseButtonsParams[bb]);
			}

			setContentView(baseLayout);

		}catch(Exception e)
		{}//{Toast.makeText(this, e.toString()+"\n\n"+e.getStackTrace()[0].toString(), Toast.LENGTH_LONG).show();}
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	private void refreshAbstracts()
	{
		if(abstracts == null)
		{
			abstracts = new ArrayList[2];
			abstracts[REQ] = new ArrayList<String[]>();
			abstracts[RES] = new ArrayList<String[]>();
		}
		if(abstractsFileName == null)
		{
			abstractsFileName = new ArrayList[2];
			abstractsFileName[REQ] = new ArrayList<String>();
			abstractsFileName[RES] = new ArrayList<String>();
		}

		File[] dataDirFiles = botDataDir.listFiles();
		for(File folder : dataDirFiles)
		{
			File[] files = folder.listFiles();

			for(int i = 0;i < files.length;i++)
			{
				File file = files[i];

				try
				{
					if(file.isFile())
					{
						//abstract/direct request-response extraction
						if(file.getName().endsWith(".req"))
						{
							abstracts[REQ].add(readFromFile(file, "SEPARATOR_NEW_LINE"));
							abstractsFileName[REQ].add(file.getName());

							File resFile = new File(folder, file.getName().replace(".req", ".res"));
							if(resFile.exists())
							{
								abstracts[RES].add(readFromFile(resFile, "SEPARATOR_NEW_LINE"));
								abstractsFileName[RES].add(resFile.getName());
							}
						}

						else

						if(file.getName().endsWith(".reqres"))
						{
							String[] ReqRes = readFromFile(file, "SEPARATOR_NEW_LINE");

							for(String reqres : ReqRes)
							{
								String[] splitString = reqres.split(splitStr, 2);
								String req = splitString[REQ];
								String res = splitString[RES];

								abstracts[REQ].add(new String[]{req});
								abstracts[RES].add(new String[]{res});
								abstractsFileName[REQ].add(file.getName());
								abstractsFileName[RES].add(file.getName());
							}
						}
					}
				}catch(IOException e)
				{}
			}
		}
	}

	@Override
	public void onBackPressed()
	{
		if(!reverse)
			closeButton.performClick();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		pendingPermissions = new ArrayList<String>();
		for(int p = 0;p < permissions.length;p++)
			if(grantResults[p] !=  PermissionChecker.PERMISSION_GRANTED)
			{
				pendingPermissions.add(permissions[p]);
				Toast.makeText(getApplicationContext(), permissionsReason[p], Toast.LENGTH_LONG).show();
			}
		if(pendingPermissions.size() > 0)
			requestPermissions(pendingPermissions.toArray(new String[]{}), CODE_COMMON);

		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())
			&& Settings.canDrawOverlays(this))
			//&& ((PowerManager)getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(getPackageName()))
			copyAssets();
		// TODO: Implement this method
		if(requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION)
		{
			if(!Settings.canDrawOverlays(this))
			{
				Toast.makeText(this, "\'Draw over other apps\' permission is required to display Chat Head", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
				startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
			}else
			if(!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName()))
			{
				Intent intentPermissionNotificationAccess = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
				startActivityForResult(intentPermissionNotificationAccess, CODE_NOTIFICATION_ACCESS);
			}else
			/*if (!((PowerManager)getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(getPackageName())) 
			{
			    Intent intentPermissionDisableBatteryOptimization = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));
			    startActivityForResult(intentPermissionDisableBatteryOptimization, CODE_BATTERY_OPTIMIZATION);
			}else*/
			if(pendingPermissions.size() > 0)
			{
				requestPermissions(pendingPermissions.toArray(new String[]{}), CODE_COMMON);
			}
		}else
		if(requestCode == CODE_NOTIFICATION_ACCESS)
		{
			if(!NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName()))
			{
				Toast.makeText(this, "\'Notification access\' is required for chat interactions", Toast.LENGTH_LONG).show();
				Intent intentPermissionNotificationAccess = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
				startActivityForResult(intentPermissionNotificationAccess, CODE_NOTIFICATION_ACCESS);
			}else
			/*if (!((PowerManager)getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(getPackageName())) 
			{
			    Intent intentPermissionDisableBatteryOptimization = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));
			    startActivityForResult(intentPermissionDisableBatteryOptimization, CODE_BATTERY_OPTIMIZATION);
			}else*/
			if(pendingPermissions.size() > 0)
			{
				requestPermissions(pendingPermissions.toArray(new String[]{}), CODE_COMMON);
			}
		}else
		/*if(requestCode == CODE_BATTERY_OPTIMIZATION)
		{
			if (!((PowerManager)getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(getPackageName())) 
			{
				Toast.makeText(this, "\'Battery Optimization\' restricts the function of Notification Listener", Toast.LENGTH_LONG).show();
			    Intent intentPermissionDisableBatteryOptimization = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));
			    startActivityForResult(intentPermissionDisableBatteryOptimization, CODE_BATTERY_OPTIMIZATION);
			}else
			if(pendingPermissions.size() > 0)
			{
				requestPermissions(pendingPermissions.toArray(new String[]{}), CODE_COMMON);
			}
		}
		else*/
			super.onActivityResult(requestCode, resultCode, data);
	}

	private void copyAssets()
	{
		int chatHeadSize = (int)(xdpi / 2.3f);
		LinearLayout initDialogLayout = new LinearLayout(getApplicationContext());
		initDialogLayout.setOrientation(LinearLayout.HORIZONTAL);
		final ImageView refreshView = new ImageView(getApplicationContext());
		refreshView.setImageResource(R.drawable.ic_refresh);
		refreshView.setScaleX(-1);
		LinearLayout.LayoutParams refreshViewParams = new LinearLayout.LayoutParams(chatHeadSize/2+chatHeadSize/2+chatHeadSize/8, chatHeadSize/2);
		refreshViewParams.setMargins(chatHeadSize/2, 0, chatHeadSize/8, 0);
		initDialogLayout.addView(refreshView, refreshViewParams);
		final CountDownTimer refreshLoadingTimer = new CountDownTimer(360*2, 1){

			@Override
			public void onTick(long p1)
			{
				refreshView.setRotation(360-p1/2);
			}

			@Override
			public void onFinish()
			{
				refreshView.setRotation(0);
				start();
			}
		};
		final TextView message = new TextView(getApplicationContext());
		message.setText("This will take some time");
		message.setTextColor(Color.LTGRAY);
		message.setGravity(Gravity.CENTER_VERTICAL);
		initDialogLayout.addView(message, new LinearLayout.LayoutParams(MATCH_PARENT, chatHeadSize/2));
		final AlertDialog initDialog = new AlertDialog.Builder(AppActivity.this).setCancelable(false).setTitle("Initializing").setView(initDialogLayout).create();
		initDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
		initDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                int titleId = getResources().getIdentifier("alertTitle", "id", "android");
                TextView title = (TextView) initDialog.findViewById(titleId);
				title.setTextColor(Color.parseColor("#01def9"));
            }
		});
		new Thread(){
            public void run(){
            	try
				{
					runOnUiThread(new Runnable() {
					    @Override
					    public void run() {
					        initDialog.show();
					        refreshLoadingTimer.start();
					    }
					});
					String[] filesPath = readFromAsset("lateFilesPath", "SEPARATOR_NEW_LINE");
					String[][] filesData = new String[filesPath.length][];
					for(int f = 0;f < filesPath.length;f++)
					{
						String filePath = filesPath[f].replace("/", File.separator);
						filesData[f] = readFromAsset(filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.length()), "SEPARATOR_NEW_LINE");
					}
					addFilesFromData(filesPath, filesData, getExternalFilesDir(null));

					//Training trainModule = new Training(SpamDir.getAbsolutePath());
					//trainModule.preProcessFiles(new String[]{"data"});
				}
				catch(Exception e)
				{
					/*runOnUiThread(new Runnable() {
					    @Override
					    public void run() {
					    	Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					    }
					});*/
				}finally{
					runOnUiThread(new Runnable() {
					    @Override
					    public void run() {
							try{
								String[] fileNames = {"spamdb", "hamdb", "arcdb", "stats"};
								for(String fileName : fileNames)
								{
									AssetManager assets = getResources().getAssets();
									InputStream in = assets.open(fileName);
									File file = new File(SpamDir, fileName);
									if(file.exists())file.delete();file.createNewFile();
							        OutputStream out = new FileOutputStream(file);
							        int nextByte;
									while ((nextByte = in.read()) != -1) {
									    out.write(nextByte);
									}

									out.flush();
								}
							}catch(IOException e){}

					    	refreshLoadingTimer.cancel();
					        initDialog.dismiss();
					        Intent intent = getIntent();
							finish();
							startActivity(intent);
					    }
					});
				}
            }
        }.start();

        try{
			String[] filesPath = readFromAsset("filesPath", "SEPARATOR_NEW_LINE");
			String[][] filesData = new String[filesPath.length][];
			for(int f = 0;f < filesPath.length;f++)
			{
				String filePath = filesPath[f].replace("/", File.separator);
				filesData[f] = readFromAsset(filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.length()), "SEPARATOR_NEW_LINE");
			}
			addFilesFromData(filesPath, filesData, dataDir);

			tgtAppsPkg = new ArrayList<String>();
			for(String tgtAppPkg : readFromFile(tgtAppsPkgFile, "SEPARATOR_NEW_LINE"))
				tgtAppsPkg.add(tgtAppPkg);

			ignoreTitles = new ArrayList<String>();
			for(String ignoreTitle : readFromFile(ignoreTitlesFile, "SEPARATOR_NEW_LINE"))
				ignoreTitles.add(ignoreTitle);

			ignoreTexts = new ArrayList<String>();
			for(String ignoreText : readFromFile(ignoreTextsFile, "SEPARATOR_NEW_LINE"))
				ignoreTexts.add(ignoreText);

			autoSendTos = new ArrayList<String>();
			for(String autoSendTo : readFromFile(autoSendTosFile, "SEPARATOR_NEW_LINE"))
				autoSendTos.add(autoSendTo);

			autoSendTosU = new ArrayList<String>();
			for(String autoSendToU : readFromFile(autoSendTosUFile, "SEPARATOR_NEW_LINE"))
				autoSendTosU.add(autoSendToU);

			autoSendMsgs = new ArrayList<String>();
			for(String autoSendMsg : readFromFile(autoSendMsgsFile, "SEPARATOR_NEW_LINE"))
				autoSendMsgs.add(autoSendMsg);

			autoSendMsgsU = new ArrayList<String>();
			for(String autoSendMsgU : readFromFile(autoSendMsgsUFile, "SEPARATOR_NEW_LINE"))
				autoSendMsgsU.add(autoSendMsgU);

			sents = new ArrayList<String>();
			for(String sent : readFromFile(sentsFile, "SEPARATOR_NEW_LINE"))
				sents.add(sent);

			ignoreds = new ArrayList<String>();
			for(String ignored : readFromFile(ignoredsFile, "SEPARATOR_NEW_LINE"))
				ignoreds.add(ignored);

			bot = new Bot(botDataDir);
			refreshAbstracts();
		}catch(IOException e){}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO: Implement this method
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		screenHeight = getResources().getDisplayMetrics().heightPixels;

		int paddingLeft = screenWidth/100;
		int paddingTop = screenHeight/100;
		int paddingRight = screenWidth/100;
		int paddingBottom = screenHeight/100;

		screenWidth -= paddingLeft + paddingRight;
		screenHeight -= paddingTop + paddingBottom;

		if(baseButtonsTotal % 2 == 0)
		{
			if(screenWidth > screenHeight)
			{
				rowsTotal = (int)(Math.round((Math.sqrt(baseButtonsTotal) * ((float)screenHeight/(float)screenWidth))));
				columnsTotal = baseButtonsTotal/rowsTotal;
			}
			else
			{
				columnsTotal = (int)(Math.round((Math.sqrt(baseButtonsTotal) * ((float)screenWidth/(float)screenHeight))));
				rowsTotal = baseButtonsTotal/columnsTotal;
			}
		}
		else
		{
			if(screenWidth > screenHeight)
			{
				rowsTotal = (int)(Math.round((Math.sqrt(baseButtonsTotal) * ((float)screenHeight/(float)screenWidth)))) + 1;
				columnsTotal = baseButtonsTotal/rowsTotal;
			}
			else
			{
				columnsTotal = (int)(Math.round((Math.sqrt(baseButtonsTotal) * ((float)screenWidth/(float)screenHeight)))) + 1;
				rowsTotal = baseButtonsTotal/columnsTotal;
			}
		}

		baseButtonsWidth = (screenWidth/(columnsTotal));
		baseButtonsHeight = (screenHeight/(rowsTotal));

		for(int bb = 0;bb < baseButtonsTotal;bb++)
			baseLayout.updateViewLayout(baseButtons[bb], baseButtonsParams[bb]);

		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
	}
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		}

		int width = drawable.getIntrinsicWidth();
		width = width > 0 ? width : 1;
		int height = drawable.getIntrinsicHeight();
		height = height > 0 ? height : 1;

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap); 
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}
	
	public ArrayList<File> parseFiles(File src, File tgt, boolean createFiles, boolean parseFileData)
	{
		ArrayList<File> filesMissing = new ArrayList<File>();

		if(src.isDirectory() && tgt.isDirectory())
		{
			File[] srcFiles = src.listFiles();
			File[] tgtFiles = tgt.listFiles();

			for(int i = 0;i < srcFiles.length;i++)
			{
				boolean found = false;

				for(int j = 0;j < tgtFiles.length;j++)
				{
					if(srcFiles[i].isFile() && tgtFiles[j].isFile() 
					   || srcFiles[i].isDirectory() && tgtFiles[j].isDirectory())
					{
						if(tgtFiles[j].getName().equals(srcFiles[i].getName()))
						{
							found = true;

							if(srcFiles[i].isDirectory())
								filesMissing.addAll(parseFiles(srcFiles[i], tgtFiles[j], createFiles, parseFileData));

							break;
						}
					}
				}

				if(!found)
				{
					filesMissing.add(srcFiles[i]);

					if(createFiles)
					{
						File fileToWrite = new File(srcFiles[i].getPath().replace(src.getPath(), tgt.getPath()));

						try
						{
							if(srcFiles[i].isFile())
							{
								fileToWrite.createNewFile();
								writeToFile(fileToWrite, readFromFile(srcFiles[i], "SEPARATOR_NEW_LINE"), "SEPARATOR_NEW_LINE");
							}
							else

							if(srcFiles[i].isDirectory())
							{
								fileToWrite.mkdirs();
								filesMissing.addAll(parseFiles(srcFiles[i], fileToWrite, createFiles, parseFileData));
							}
						}catch(IOException e)
						{}
					}
				}
			}
		}
		return filesMissing;
	}

	public boolean addFilesFromData(String[] filesPath, String[][] filesData, File tgt)
	{
		try
		{
			for(int f = 0;f < filesPath.length;f++)
			{
				String filePath = filesPath[f];
				File file = new File(tgt, filePath);

				String[] pathSplit = filePath.split(File.separator);
				File parent = tgt;
				for(int fl=1;fl<pathSplit.length-1;fl++)
				{
					String folderPath = pathSplit[fl];
					File folder = new File(parent, folderPath);
					if(!folder.exists())
						folder.mkdir();
					parent = folder;
				}
				if(!file.exists())
				{
					file.createNewFile();
					writeToFile(file, filesData[f], "SEPARATOR_NEW_LINE");
				}
			}

			return true;
		}catch(Exception e)
		{
			return false;
		}
	}

	public boolean parseFile(File srcFile, File tgtFile, String srcSeparator, String tgtSeparator)
	{
		try
		{
			String[] srcData = readFromFile(srcFile, srcSeparator);
			String[] tgtData = readFromFile(tgtFile, tgtSeparator);
			ArrayList<String> dataToWriteList = new ArrayList<String>();

			for(String srcStr : srcData)
			{
				boolean isRepeated = false;

				for(String tgtStr : tgtData)
					if(tgtStr.trim().equalsIgnoreCase(srcStr.trim()))
						isRepeated = true;

				if(!isRepeated)
					dataToWriteList.add(srcStr);
			}

			String[] dataToWriteArray = dataToWriteList.toArray(new String[]{});

			writeToFile(tgtFile, dataToWriteArray, tgtSeparator);

			return true;
		}catch (IOException e)
		{
			return false;
		}
	}

	/*public File[] listFilesFromNames(String[] names)
	 {
	 File[] files = new File[names.length];
	 for(int i = 0;i < names.length;i++)
	 files[i] = new File(names[i]);

	 return files;
	 }*/

	public boolean writeToFile(File file, String[] data, String SEPARATOR) throws IOException
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

	public String[] readFromAsset(String name, String SEPARATOR) throws IOException
	{
		AssetManager assets = getResources().getAssets();
		InputStream inputStream = assets.open(name);
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

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
	}
}
