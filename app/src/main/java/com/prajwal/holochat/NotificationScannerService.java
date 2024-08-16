package com.prajwal.holochat;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.service.notification.StatusBarNotification;
import android.service.notification.NotificationListenerService;
import java.io.*;
import java.util.*;
import androidx.core.app.NotificationCompat;
import com.google.firebase.analytics.FirebaseAnalytics;

//import android.widget.Toast;

public class NotificationScannerService extends NotificationListenerService
{ 
    File dataDir;
	File NLdataDir;
	File CHDataDir;
	File tgtAppsPkgFile;
	File ignoreTitlesFile;
	File ignoreTextsFile;
	File keepNotificationsFile;
	boolean NLConnected = false;
	boolean chatHeadCreated;
	int SENDER = 0;
	int MESSAGE = 1;
	boolean created = false;
	boolean destroyed = true;
	boolean updated = true;
	ArrayList<StatusBarNotification> notesToSend = new ArrayList<StatusBarNotification>();
	ArrayList<StatusBarNotification> sbns = new ArrayList<StatusBarNotification>();
	//Icon buttIcon;
	Icon likeIcon;
	Icon heartIcon;
	Icon doubleTickIcon;
	Icon zzzIcon;

	String groupConStr = " @ ";

	String[] targetPkgs;

	String[] ignoreTitles;

	String[] ignoreTexts;

	String[] prepareTexts = new String[]{
		"Checking for new messages"
	};

	String[] notSenderStrs = new String[]{
		"http",
		"video",
		"audio",
		"voice message",
		"message"
	};

	private FirebaseAnalytics mFirebaseAnalytics;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO: Implement this method
		if(intent != null && NLConnected)
			switch(intent.getStringExtra("type"))
			{
				case "created" :

					created = true;
					destroyed = false;

					if(notesToSend.size() > 0)
					{
						if(sendNotification(NotificationScannerService.class, notesToSend.get(0)))
						{
							startService(new Intent(NotificationScannerService.this, ChatHeadService.class)
							 .putExtra("type", "update")
							 .putExtra("data", new String[][]{{"Bot"}, {"hello"}})
							 .putExtra("icon", Icon.createWithResource(NotificationScannerService.this, R.drawable.ic_logo_small))
							 .putExtra("id", String.valueOf(new Random().nextInt()))
							 //.putExtra("channel", "channel")
							 .putExtra("bIcon", (Icon)null)
							 .putExtra("image", (Bitmap)null)
							 .putExtra("pkg", getPackageName()));
							/*new CountDownTimer(1000, 1000){
								@Override
								public void onTick(long p1)
								{
									// TODO: Implement this method
								}

								@Override
								public void onFinish()
								{
									startService(new Intent(NotificationScannerService.this, ChatHeadService.class)
									 .putExtra("type", "update")
									 .putExtra("data", new String[][]{{"Bot"}, {"hello"}})
									 .putExtra("icon", Icon.createWithResource(NotificationScannerService.this, R.drawable.ic_logo_small))
									 .putExtra("id", String.valueOf(new Random().nextInt()))
									 //.putExtra("channel", "channel")
									 .putExtra("bIcon", (Icon)null)
									 .putExtra("image", (Bitmap)null)
									 .putExtra("pkg", getPackageName()));
								}
							}.start();*/
							sbns.add(notesToSend.get(0));
							try{
								if(!Boolean.parseBoolean(readFromFile(keepNotificationsFile, "SEPARATOR_NEW_LINE")[0]))
									cancelNotification(notesToSend.get(0).getKey());
							}
							catch(IOException e)
							{}
						}
						notesToSend.remove(0);
					}

					break;

				case "updated" :

					updated = true;

					if(notesToSend.size() > 0)
					{
						if(sendNotification(ChatHeadService.class, notesToSend.get(0)))
						{
							sbns.add(notesToSend.get(0));
							try{
								if(!Boolean.parseBoolean(readFromFile(keepNotificationsFile, "SEPARATOR_NEW_LINE")[0]))
									cancelNotification(notesToSend.get(0).getKey());
							}
							catch(IOException e)
							{}
						}
						notesToSend.remove(0);
					}

					break;

				case "restartMe" :

					new CountDownTimer(5000, 1){
						@Override
						public void onTick(long p1)
						{
							if(!isMyServiceRunning(ChatHeadService.class))
							{
								destroyed = true;
							 	updated = true;
								initService(ChatHeadService.class);
								updated = false;
								notesToSend = sbns;
								cancel();
							}
						}

						@Override
						public void onFinish()
						{}
					}.start();

					break;

				case "destroyed" :

					destroyed = true;
					created = false;
					int sentsCount = intent.getIntExtra("sentsCount", 0);
					int spamsCount = intent.getIntExtra("spamsCount", 0);
					if(mFirebaseAnalytics != null)
					{
						Bundle bundle = new Bundle();
						bundle.putInt("messages_count", sbns.size());
						bundle.putInt("replies_count", sentsCount);
						bundle.putInt("spams_count", spamsCount);
						mFirebaseAnalytics.logEvent("chat_head_closed", bundle);
					}
					sbns = new ArrayList<StatusBarNotification>();

					break;
				
				case "update" :
					
					try
					{
						targetPkgs = readFromFile(tgtAppsPkgFile, "SEPARATOR_NEW_LINE");
						ignoreTitles = readFromFile(ignoreTitlesFile, "SEPARATOR_NEW_LINE");
						ignoreTexts = readFromFile(ignoreTextsFile, "SEPARATOR_NEW_LINE");
					}
					catch(IOException e)
					{}
					break;
				
				case "replyIntentsReq" :

					try
					{
						boolean isReplyActive = false;
						String id = intent.getStringExtra("id");
						int chatIndex = intent.getIntExtra("chatIndex", -1);
						String sender = intent.getStringExtra("sender");
						String message = intent.getStringExtra("message");
						String reply = intent.getStringExtra("reply");

						for(int s=0;s<sbns.size();s++)
						{
							StatusBarNotification sbn = sbns.get(s);
							if(id.equals(sbn.getKey()))
							{
								isReplyActive = true;
								Notification n = sbn.getNotification();
								Bundle localBundle = n.extras;
								//Notification.Action action = null;
								
								String template = "";

									try
									{
										template = n.extras.getString(Notification.EXTRA_TEMPLATE);
									}catch(Exception e)
									{
										template = null;
									}finally
									{
										/*boolean isMessagingStyle = template.equalsIgnoreCase("android.app.Notification$MessagingStyle");
										if(isMessagingStyle)
										{
											Notification.MessagingStyle.Message[] msgs = (Notification.MessagingStyle.Message[]) sbn.getNotification().extras.get(Notification.EXTRA_MESSAGES);

											if(msgs != null && msgs.length > 0)
											{
												for(int i = 0;i < msgs.length;i++)
												{
													if(sender.trim().equalsIgnoreCase(msgs[i].getSender().toString().trim()))
													{
														boolean equals = false;
														
														if(message.trim().equalsIgnoreCase(msgs[i].getText().toString().trim()))
														{
															equals = true;
														}
														else
														{
															int sCount = 0;
															for(Notification.MessagingStyle.Message msg : msgs)
															{
																if(msg.getSender().toString().trim().equalsIgnoreCase(sender.trim()))
																{
																	sCount++;
																}
															}
															if(sCount < 2)
																equals = true;
														}
														
														if(equals)
														{
															int actionPerMsg = actions.length/msgs.length;

															if((i+1)*actionPerMsg <= actions.length)
															{
																for(int a = actionPerMsg*i;a < actionPerMsg*(i+1);a++)
																	if(actions[a] != null && actions[a].getAllowGeneratedReplies() && actions[a].actionIntent != null)
																	{
																		action = actions[a];
																	}
																break;
															}
														}
													}
												}
											}
										}*/

										//if(action == null)
										//{

										NotificationCompat.Action action = null;

											for(int i = 0; i < NotificationCompat.getActionCount(n); i++)
											{
												NotificationCompat.Action act = NotificationCompat.getAction(n, i);
												if(act != null)
	            									if((act.title.toString().toLowerCase().contains("reply") || act.title.toString().toLowerCase().contains("message")) && act.actionIntent != null)
	                								{
	                									action = act;
	                									break;
	                								}
											}
										//}

										if(action != null && action.getRemoteInputs() != null)
										{
											PendingIntent pendingIntent = action.actionIntent;

												androidx.core.app.RemoteInput[] remoteInputs = action.getRemoteInputs();

												//filling the bundle with our message
												for(androidx.core.app.RemoteInput rIn : remoteInputs)
													localBundle.putCharSequence(rIn.getResultKey(), reply);

												//creating an intent to be sent to pendingIntent
												Intent localIntent = new Intent();
												//localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

												//filling the localIntent with message from localBundle
												androidx.core.app.RemoteInput.addResultsToIntent(remoteInputs, localIntent, localBundle);

												/*Intent tmpIntent = new Intent();
												   Bundle bundle = new Bundle();
												   ArrayList<android.app.RemoteInput> actualInputs = new ArrayList<>();

													//tmpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
												   for (androidx.core.app.RemoteInput input : remoteInputs)
												   {
												      bundle.putCharSequence(input.getResultKey(), reply);
												      android.app.RemoteInput.Builder builder = new android.app.RemoteInput.Builder(input.getResultKey());
												      builder.setLabel(input.getLabel());
												      builder.setChoices(input.getChoices());
												      //builder.setAllowFreeFormInput(input.isAllowFreeFormInput());
												      builder.addExtras(input.getExtras());
												      actualInputs.add(builder.build());
												   }

												   android.app.RemoteInput[] inputs = actualInputs.toArray(new android.app.RemoteInput[actualInputs.size()]);
												   android.app.RemoteInput.addResultsToIntent(inputs, tmpIntent, bundle);
												*/
												Intent replyIntent = new Intent(NotificationScannerService.this, ChatHeadService.class);
												replyIntent.putExtra("type", "replyIntentsRes");
												replyIntent.putExtra("res", "ready");
												replyIntent.putExtra("chatIndex", chatIndex);
												replyIntent.putExtra("id", id);
												replyIntent.putExtra("pIntent", pendingIntent);
												replyIntent.putExtra("lIntent", localIntent);
												startService(replyIntent);
												break;
										}
										else
										{		Intent replyIntent = new Intent(NotificationScannerService.this, ChatHeadService.class);
												replyIntent.putExtra("type", "replyIntentsRes");
												replyIntent.putExtra("res", "notSupported");
												replyIntent.putExtra("chatIndex", chatIndex);
												replyIntent.putExtra("id", id);
												replyIntent.putExtra("reply", reply);
												replyIntent.putExtra("pIntent", n.contentIntent);
												startService(replyIntent);
										}
									}
							}
						}

						if(!isReplyActive)
						{
							Intent replyIntent = new Intent(NotificationScannerService.this, ChatHeadService.class);
							replyIntent.putExtra("type", "replyIntentsRes");
							replyIntent.putExtra("res", "notFound");
							replyIntent.putExtra("chatIndex", chatIndex);
							replyIntent.putExtra("reply", reply);
							replyIntent.putExtra("id", id);
							startService(replyIntent);
						}
					}catch(Exception e)
					{
						//sendError(ChatHeadService.class, e);
					}
					break;

				case "buttonIntentsReq" :

					boolean isButtonActive = false;
					String buttonId = intent.getStringExtra("id");
					//Notification.Action action = null;

					try
					{
						for(int s=0;s<sbns.size();s++)
						{
							StatusBarNotification sbn = sbns.get(s);
							if(buttonId.equals(sbn.getKey()))
							{
								isButtonActive = true;
								Notification n = sbn.getNotification();
								
								//Notification.Action[] actions = sbn.getNotification().actions;

									String template = "";

									try
									{
										template = n.extras.getString(Notification.EXTRA_TEMPLATE);
									}catch(Exception e)
									{
										template = null;
									}finally
									{
										/*boolean isMessagingStyle = template.equalsIgnoreCase("android.app.Notification$MessagingStyle");
										
										if(isMessagingStyle)
										{
											Notification.MessagingStyle.Message[] msgs = (Notification.MessagingStyle.Message[]) sbn.getNotification().extras.get(Notification.EXTRA_MESSAGES);

											if(msgs != null && msgs.length > 0)
											{
												for(int i = 0;i < msgs.length;i++)
												{
													boolean equals = false;

													if(message.trim().equalsIgnoreCase(msgs[i].getText().toString().trim()))
													{
														equals = true;
													}
													else
													{
														int sCount = 0;
														for(Notification.MessagingStyle.Message msg : msgs)
														{
															if(msg.getSender().toString().trim().equalsIgnoreCase(sender.trim()))
															{
																sCount++;
															}
														}
														if(sCount < 2)
															equals = true;
													}
													
													if(equals)
													{
														if(i < actions.length)
														{
															int actionPerMsg = actions.length/msgs.length;

															if((i+1)*actionPerMsg <= actions.length)
															{
																for(int a = actionPerMsg*i;a < actionPerMsg*(i+1);a++)
																	if(actions[a] != null && !(actions[a].title.toString().toLowerCase().contains("reply") || actions[a].title.toString().toLowerCase().contains("message")) && actions[a].actionIntent != null)
																	{
																		action = actions[a];
																	}
																break;
															}
														}
													}
												}
											}
										}*/

										//if(action == null)
										//{

										NotificationCompat.Action action = null;

											for(int i = 0; i < NotificationCompat.getActionCount(n); i++)
											{
												NotificationCompat.Action act = NotificationCompat.getAction(n, i);
												if(act != null)
		            								if(!(act.title.toString().toLowerCase().contains("reply") || act.title.toString().toLowerCase().contains("message")) && act.actionIntent != null)
		                							{
	                									action = act;
	                									break;
	                								}
											}
										//}

										if(action != null)
										{
												PendingIntent pendingIntent = action.actionIntent;

												//creating an intent to be sent to pendingIntent
												
												Intent localIntent = new Intent();
												//localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
												
												//Intent tmpIntent = new Intent();
												
												Intent replyIntent = new Intent(NotificationScannerService.this, ChatHeadService.class);
												replyIntent.putExtra("type", "buttonIntentsRes");
												replyIntent.putExtra("res", "ready");
												replyIntent.putExtra("id", buttonId);
												replyIntent.putExtra("pIntent", pendingIntent);
												replyIntent.putExtra("lIntent", localIntent);
												startService(replyIntent);
												break;
										}
									}
							}
						}

						if(!isButtonActive)
						{
							Intent replyIntent = new Intent(NotificationScannerService.this, ChatHeadService.class);
							replyIntent.putExtra("type", "buttonIntentsRes");
							replyIntent.putExtra("res", "notFound");
							replyIntent.putExtra("id", buttonId);
							startService(replyIntent);
						}
					}catch(Exception e)
					{
						//sendError(ChatHeadService.class, e);
					}
					break;

				case "profileIntentsReq" :

					boolean isNotificationActive = false;
					String notificationId = intent.getStringExtra("id");
					int chatIndex = intent.getIntExtra("chatIndex", -1);
					//Notification.Action action = null;

					try
					{
						for(int s=0;s<sbns.size();s++)
						{
							StatusBarNotification sbn = sbns.get(s);
							if(notificationId.equals(sbn.getKey()))
							{
								isNotificationActive = true;
								Notification n = sbn.getNotification();
								//Notification.Action[] actions = sbn.getNotification().actions;

									String template = "";

									try
									{
										template = n.extras.getString(Notification.EXTRA_TEMPLATE);
									}catch(Exception e)
									{
										template = null;
									}finally
									{
										/*boolean isMessagingStyle = template.equalsIgnoreCase("android.app.Notification$MessagingStyle");
										
										if(isMessagingStyle)
										{
											Notification.MessagingStyle.Message[] msgs = (Notification.MessagingStyle.Message[]) sbn.getNotification().extras.get(Notification.EXTRA_MESSAGES);

											if(msgs != null && msgs.length > 0)
											{
												for(int i = 0;i < msgs.length;i++)
												{
													boolean equals = false;

													if(message.trim().equalsIgnoreCase(msgs[i].getText().toString().trim()))
													{
														equals = true;
													}
													else
													{
														int sCount = 0;
														for(Notification.MessagingStyle.Message msg : msgs)
														{
															if(msg.getSender().toString().trim().equalsIgnoreCase(sender.trim()))
															{
																sCount++;
															}
														}
														if(sCount < 2)
															equals = true;
													}
													
													if(equals)
													{
														if(i < actions.length)
														{
															int actionPerMsg = actions.length/msgs.length;

															if((i+1)*actionPerMsg <= actions.length)
															{
																for(int a = actionPerMsg*i;a < actionPerMsg*(i+1);a++)
																	if(actions[a] != null && !(actions[a].title.toString().toLowerCase().contains("reply") || actions[a].title.toString().toLowerCase().contains("message")) && actions[a].actionIntent != null)
																	{
																		action = actions[a];
																	}
																break;
															}
														}
													}
												}
											}
										}*/

										//if(action == null)
										//{
												PendingIntent pendingIntent = n.contentIntent;

												Intent replyIntent = new Intent(NotificationScannerService.this, ChatHeadService.class);
												replyIntent.putExtra("type", "profileIntentsRes");
												replyIntent.putExtra("res", "ready");
												replyIntent.putExtra("id", notificationId);
												replyIntent.putExtra("pIntent", pendingIntent);
												startService(replyIntent);
												break;
									}
							}
						}

						if(!isNotificationActive)
						{
							Intent replyIntent = new Intent(NotificationScannerService.this, ChatHeadService.class);
							replyIntent.putExtra("type", "profileIntentsRes");
							replyIntent.putExtra("res", "notFound");
							replyIntent.putExtra("id", notificationId);
							replyIntent.putExtra("chatIndex", chatIndex);
							startService(replyIntent);
						}
					}catch(Exception e)
					{
						//sendError(ChatHeadService.class, e);
					}
					break;
			}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onListenerConnected()
	{
		// TODO: Implement this method
		super.onListenerConnected(); 
		NLConnected = true;
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		
			/*Intent launchIntent = new Intent(getApplicationContext(), AppActivity.class);
			launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		    Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
		    Paint paint = new Paint();
		    paint.setColor(0xFF808080); // gray
		    paint.setTextAlign(Paint.Align.CENTER);
		    paint.setTextSize(50);
		    new Canvas(bitmap).drawText("69", 50, 50, paint);

		    Intent addIntent = new Intent();
		    addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);
		    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "SmartChatIO");
		    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
		    addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		    getApplicationContext().sendBroadcast(addIntent);*/

		dataDir = getFilesDir();
		NLdataDir = new File(dataDir, "NotificationListenerData");
		CHDataDir = new File(dataDir, "ChatHeadData");
		tgtAppsPkgFile = new File(NLdataDir, "tgtAppsPkg");
		ignoreTitlesFile = new File(NLdataDir, "ignoreTitles");
		ignoreTextsFile = new File(NLdataDir, "ignoreTexts");
		keepNotificationsFile = new File(CHDataDir, "keepNotifications");

		try
		{
			targetPkgs = readFromFile(tgtAppsPkgFile, "SEPARATOR_NEW_LINE");
			ignoreTitles = readFromFile(ignoreTitlesFile, "SEPARATOR_NEW_LINE");
			ignoreTexts = readFromFile(ignoreTextsFile, "SEPARATOR_NEW_LINE");
		}catch (IOException e)
		{}

		//buttIcon = Icon.createWithResource(getApplicationContext(), R.drawable.circle_filled_blue);
		likeIcon = Icon.createWithResource(getApplicationContext(), R.drawable.like);
		heartIcon = Icon.createWithResource(getApplicationContext(), R.drawable.heart);
		doubleTickIcon = Icon.createWithResource(getApplicationContext(), R.drawable.double_tick);
		zzzIcon = Icon.createWithResource(getApplicationContext(), R.drawable.zzz);

		/*if(destroyed)
		{
			initService(ChatHeadService.class);
		}*/
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn, NotificationScannerService.RankingMap rankingMap)
	{ 
		// TODO: Implement this method
		super.onNotificationPosted(sbn, rankingMap);
		try{
		String pkg = sbn.getPackageName();

		for(String targetPkg : targetPkgs)
			if(pkg.toLowerCase().equals(targetPkg))
			{
				String title = sbn.getNotification().extras.get(Notification.EXTRA_TITLE).toString();
				String text = sbn.getNotification().extras.get(Notification.EXTRA_TEXT).toString();

				//Toast.makeText(getApplicationContext(), "title:"+title+"\n"+"text:"+text, Toast.LENGTH_LONG).show();

				boolean ignore = false;

				for(String ignoreText : ignoreTexts)
					if(text.equals(ignoreText))
						ignore = true;

				for(String ignoreTitle : ignoreTitles)
					if(title.equals(ignoreTitle))
						ignore = true;

				if(!ignore)
				{
					if(created)
					{
						if(updated)
						{
							//Toast.makeText(getApplicationContext(), "sendNotification()", Toast.LENGTH_LONG).show();
							if(sendNotification(ChatHeadService.class, sbn))
							{
								sbns.add(sbn);
								if(!Boolean.parseBoolean(readFromFile(keepNotificationsFile, "SEPARATOR_NEW_LINE")[0]))
									cancelNotification(sbn.getKey());
							}
						}
						else
						{
							notesToSend.add(sbn);
						}
					}
					else
					{
						if(shouldSendNotification(sbn))
						{
							initService(ChatHeadService.class);
							updated = false;
							notesToSend.add(sbn);
						}
					}
				}//else
					//Toast.makeText(getApplicationContext(), "ignored", Toast.LENGTH_LONG).show();

				/*if(destroyed)
					for(String pprText : prepareTexts)
						if(text.equals(pprText))
						{
							initService(ChatHeadService.class);
						}*/
			}}catch(Exception e){
				//sendError(ChatHeadService.class, e);
			}
	}


	@Override
	public void onNotificationRemoved(StatusBarNotification sbn, NotificationScannerService.RankingMap rankingMap, int reason)
	{
		// TODO: Implement this method
		//removeNotification(ChatHeadService.class, sbn);
		super.onNotificationRemoved(sbn, rankingMap, reason);
	}

	@Override
	public void onListenerDisconnected()
	{
		// TODO: Implement this method
		super.onListenerDisconnected();
		NLConnected = false;
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

	public String[][] getNotificationData(StatusBarNotification sbn)
	{
		//sender-message data to return
		String[][] data = new String[2][];

		//notification details
		String pkg = sbn.getPackageName();
		Notification n = sbn.getNotification();
		String title = n.extras.get(Notification.EXTRA_TITLE).toString();
		String template = "";
		boolean titleIsAppName = false;
		int maxNameLength = 30;

		try
		{
			template = n.extras.getString(Notification.EXTRA_TEMPLATE);
			//Toast.makeText(getApplicationContext(), template, Toast.LENGTH_LONG).show();
		}catch(Exception e)
		{
			template = null;
		}finally
		{
			if(pkg.contains(title.toLowerCase()))
				titleIsAppName = true;

			//if(template == null || template.equalsIgnoreCase("android.app.Notification$MessagingStyle"))
			//{
				data[SENDER] = new String[1];
				data[MESSAGE] = new String[1];

				String text = n.extras.get(Notification.EXTRA_TEXT).toString();

				if(!titleIsAppName)
				{
					if(text.contains(":"))
					{
						String[] splitText = text.split(":");

						boolean isSender = true;
						for(String notSenderStr : notSenderStrs)
							if(splitText[0].toLowerCase().contains(notSenderStr))
								isSender = false;

						if(splitText[0].length() <= maxNameLength && isSender)
						{//group conversation e.g. Freinds [Max : yo \n Harry :hey]
							data[SENDER][0] = splitText[0] + groupConStr + title;
							data[MESSAGE][0] = "";
							for(int s = 1;s < splitText.length;s++)
								data[MESSAGE][0] += (s > 1? ":" : "") + splitText[s];
							//Toast.makeText(getApplicationContext(), "sender:"+data[SENDER][0]+"\n"+"message:"+data[MESSAGE][0], Toast.LENGTH_LONG).show();
						}
						else
						{//message from single user like John [hello]
							data[SENDER][0] = title;
							data[MESSAGE][0] = text;
						}
					}
					else
					{//same
						data[SENDER][0] = title;
						data[MESSAGE][0] = text;
					}
				}
				else
				{
					if(text.contains(":"))
					{//like WhatsApp [Max : hi]
						String[] splitText = text.split(":");
						data[SENDER][0] = splitText[0];
						data[MESSAGE][0] = "";
						for(int s = 1;s < splitText.length;s++)
							data[MESSAGE][0] += (s > 1? ":" : "") + splitText[s];

					}
					else
					{//unsupported ones
						data[SENDER][0] = "(unsupported)" + title;
						data[MESSAGE][0] = text;
					}
				}
			//}

			/*else

			if(template.equalsIgnoreCase("android.app.Notification$InboxStyle"))
			{
				CharSequence[] texts = sbn.getNotification().extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);

				if(texts != null && texts.length > 0)
				{
					data[SENDER] = new String[texts.length];
					data[MESSAGE] = new String[texts.length];

					for(int i = 0;i < texts.length;i++)
					{
						String text = texts[i].toString();

						if(!titleIsAppName)
						{
							if(text.contains(":"))
							{
								String[] splitText = text.split(":");

								boolean isSender = true;
								for(String notSenderStr : notSenderStrs)
									if(splitText[0].toLowerCase().contains(notSenderStr))
										isSender = false;

								if(splitText[0].length() <= maxNameLength && isSender)
								{
									data[SENDER][i] = splitText[0] + groupConStr + title;
									data[MESSAGE][i] = "";
									for(int s = 1;s < splitText.length;s++)
										data[MESSAGE][i] += (s > 1? ":" : "") + splitText[s];

									Toast.makeText(getApplicationContext(), "sender:"+data[SENDER][i]+"\n"+"message:"+data[MESSAGE][i], Toast.LENGTH_LONG).show();
								}
								else
								{
									data[SENDER][i] = title;
									data[MESSAGE][i] = text;
								}
							}
							else
							{
								data[SENDER][i] = title;
								data[MESSAGE][i] = text;
							}
						}
						else
						{
							if(text.contains(":"))
							{
								String[] splitText = text.split(":");

								boolean isSender = true;
								for(String notSenderStr : notSenderStrs)
									if(splitText[0].toLowerCase().contains(notSenderStr))
										isSender = false;

								if(splitText[0].length() <= maxNameLength && isSender)
								{
									data[SENDER][i] = splitText[0];
									data[MESSAGE][i] = "";
									for(int s = 1;s < splitText.length;s++)
										data[MESSAGE][i] += (s > 1? ":" : "") + splitText[s];

								}
								else
								{
									data[SENDER][i] = "(unsupported)" + title;
									data[MESSAGE][i] = text;
								}
							}
							else
							{
								data[SENDER][i] = "(unsupported)" + title;
								data[MESSAGE][i] = text;
							}
						}
					}
				}
			}*/

			/*else

			if(template.equalsIgnoreCase("android.app.Notification$MessagingStyle"))
			{
				Notification.MessagingStyle.Message[] msgs = (Notification.MessagingStyle.Message[]) sbn.getNotification().extras.get(Notification.EXTRA_MESSAGES);
				
				if(msgs != null && msgs.length > 0)
				{
					data[SENDER] = new String[msgs.length];
					data[MESSAGE] = new String[msgs.length];

					for(int i = 0;i < msgs.length;i++)
					{
						data[SENDER][i] = msgs[i].getSender().toString();
						data[MESSAGE][i] = msgs[i].getText().toString();

						Toast.makeText(getApplicationContext(), "sender:"+data[SENDER][i]+"\n"+"message:"+data[MESSAGE][i], Toast.LENGTH_LONG).show();
					}
				}
			}*/

			/*else

			if(template.equalsIgnoreCase("android.app.Notification$BigTextStyle"))
			{
				String msg = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_BIG_TEXT).toString();

				if(msg != null && !msg.isEmpty())
				{
					data[SENDER] = new String[1];
					data[MESSAGE] = new String[1];

					String bigTitle = "";
					String bigText = "";

					try
					{
						bigTitle = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE_BIG).toString();
						bigText = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_BIG_TEXT).toString();
					}catch(Exception e)
					{
					}finally
					{
						String text = sbn.getNotification().extras.get(Notification.EXTRA_TEXT).toString();
						data[SENDER][0] = (bigTitle != null && !bigTitle.isEmpty()) ? bigTitle : title;
						data[MESSAGE][0] = (bigText != null && !bigText.isEmpty()) ? bigText : text;
						Toast.makeText(getApplicationContext(), "sender:"+data[SENDER][0]+"\n"+"message:"+data[MESSAGE][0], Toast.LENGTH_LONG).show();
						return data;
					}
				}
			}*/

			/*else

			if(template.equalsIgnoreCase("android.app.Notification$BigPictureStyle"))
			{
				Bitmap img = (Bitmap) sbn.getNotification().extras.get(Notification.EXTRA_PICTURE);

				if(img != null)
				{
					data[SENDER] = new String[1];
					data[MESSAGE] = new String[1];

					try
					{
						String bigTitle = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE_BIG).toString();
						data[SENDER][0] = bigTitle;
					}catch(Exception e)
					{
						data[SENDER][0] = title;
					}finally
					{
						String text = sbn.getNotification().extras.get(Notification.EXTRA_TEXT).toString();
						data[MESSAGE][0] = (text != null && !text.isEmpty()) ? text + "<image>" : "<image>";
						return data;
					}
				}
			}*/
		 	return data;
		}
	}

	public String[][] adaptData(String[][] data, String pkg)
	{
		//adaptation according to various apps
		for(int i = 0;i < data[SENDER].length;i++)
		{
			//whatsapp group message
			/*if(pkg.equals("com.whatsapp"))
			 if(data[SENDER][i].contains("@"))
			 data[SENDER][i] = data[SENDER][i].replaceFirst("@", groupConStr);*/

			//facebook reaction message (for e.g. "mark zuckerburg : mark zuckerburg reacted to your reply")
			if(pkg.equals("com.facebook.orca"))
				if(data[MESSAGE][i].trim().startsWith(data[SENDER][i].trim()))
					data[MESSAGE][i] = data[MESSAGE][i].replaceFirst(data[SENDER][i], "");
		}
		return data;
	}

	public boolean initService(Class c)
	{
		try
		{
			Intent intent = new Intent(NotificationScannerService.this, c);
			intent.putExtra("type", "init");
			startService(intent);

			return true;
		}catch(Exception e) 
		{
			//sendError(c, e);
			return false;
		}
	}

	public boolean sendNotification(Class c, StatusBarNotification sbn)
	{
		try
		{
			Notification n = sbn.getNotification();
		    String pkg = sbn.getPackageName().toLowerCase();
			
			if(shouldSendNotification(sbn))
			{
				Intent intentSendNotes = new Intent(NotificationScannerService.this, c);
				intentSendNotes.putExtra("type", "update");
				intentSendNotes.putExtra("data", adaptData(getNotificationData(sbn), sbn.getPackageName()));
				intentSendNotes.putExtra("pkg", sbn.getPackageName());
				intentSendNotes.putExtra("id", sbn.getKey());
				//intentSendNotes.putExtra("channel", sbn.getNotification().getChannelId());
				intentSendNotes.putExtra("icon", n.getLargeIcon() != null ? n.getLargeIcon() : n.getSmallIcon());
				Icon bIcon = null;
				String bText = null;
				if(n.actions != null)
					for(int i = 0; i < NotificationCompat.getActionCount(n); i++)
					{
						NotificationCompat.Action act = NotificationCompat.getAction(n, i);
						String actTitle = act.title.toString().toLowerCase();
						if(act != null)
							if(!(actTitle.contains("reply") || act.title.toString().toLowerCase().contains("message") || pkg.equals("com.whatsapp")) && act.actionIntent != null)
							{
								if(pkg.contains("insta") || pkg.contains("viber") || actTitle.contains("heart") || actTitle.contains("love"))
								{
									bIcon = heartIcon;
								}else
								if(actTitle.contains("like") || actTitle.contains("thumb"))
								{
										bIcon = likeIcon;
								}else
								if(actTitle.contains("mark"))
								{
									bIcon = doubleTickIcon;
								}else
								if(actTitle.contains("snooze") || actTitle.contains("mute") || actTitle.contains("silent"))
								{
									bIcon = zzzIcon;
								}

								bText = actTitle;
								break;
							}
					}
				intentSendNotes.putExtra("bIcon", bIcon);
			    intentSendNotes.putExtra("bText", bText);

				if(n.extras.get(Notification.EXTRA_PICTURE) != null)
					intentSendNotes.putExtra("image", (Bitmap) n.extras.get(Notification.EXTRA_PICTURE));
				else
					intentSendNotes.putExtra("image", (Bitmap)null);

				startService(intentSendNotes);
				updated = false;
				return true;
			}else
				return false;
		}catch(Exception e)
		{
			//sendError(c, e);
			return false;
		}
	}

	public boolean shouldSendNotification(StatusBarNotification sbn)
	{
		Notification n = sbn.getNotification();
		String channelId = n.getChannelId();
		String pkg = sbn.getPackageName().toLowerCase();

		boolean shouldSend = true;
		if(pkg.equals("com.whatsapp"))
			shouldSend = channelId.contains("chat");
		else
		if(pkg.equals("org.telegram.messenger"))
			shouldSend = channelId.contains("private") || channelId.contains("groups");
		else
		if(pkg.equals("com.facebook.orca"))
			shouldSend = channelId.contains("messaging");
		else
		if(pkg.equals("com.skype.raider"))
			shouldSend = channelId.contains("messages");
		else
		if(pkg.equals("com.viber.voip"))
			shouldSend = channelId.contains("messages") || channelId.equals("smart");
		else
		if(pkg.equals("com.imo.android.imoim"))
			shouldSend = channelId.equals("notification1") || channelId.equals("group1");
		else
		if(pkg.equals("org.thoughtcrime.securesms"))
			shouldSend = channelId.equals("messages_1");
		else
		if(pkg.equals("com.zing.zalo"))
			shouldSend = channelId.contains("chat") || channelId.contains("group");
		else
		if(pkg.equals("jp.naver.line.android"))
			shouldSend = channelId.contains("NewMessages");
		else
		if(pkg.equals("kik.android"))
			shouldSend = channelId.equals("default_messages_channel_id_v2");
		return shouldSend;
	}

	public boolean removeNotification(Class c, StatusBarNotification sbn)
	{
		try
		{
			Intent intent = new Intent(NotificationScannerService.this, c);
			intent.putExtra("type", "remove");
			intent.putExtra("id", sbn.getKey());
			startService(intent);
			return true;
		}catch(Exception e)
		{
			//sendError(c, e);
			return false;
		}
	}

	public boolean sendError(Class c, Exception ex)
	{
		try
		{
			Intent intent = new Intent(NotificationScannerService.this, c);
			intent.putExtra("type", "error");
			intent.putExtra("error", ex.toString() + ex.getStackTrace()[0].toString());
			startService(intent);

			return true;
		}catch(Exception e)
		{
			return false;
		}
	}

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
}