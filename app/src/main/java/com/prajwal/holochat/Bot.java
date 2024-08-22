package com.prajwal.holochat;

import java.io.*;
import java.util.*;

public class Bot
{
	static final int TYPE_ASSERTIVE = 0;
	static final int TYPE_IMPERATIVE = 1;
	static final int TYPE_INTERROGATIVE = 2;
	static final int TYPE_EXCLAMATORY = 3;
	static final int TYPE_ABSTRACT = 4;
	static final int TYPE_LENGTH = 4;
	static final int TENSE_PRESENT = 0;
	static final int TENSE_PRESENT_PROGRESSIVE = 1;
	static final int TENSE_PRESENT_PERFECT = 2;
	static final int TENSE_PRESENT_PERFECT_PROGRESSIVE = 3;
	static final int TENSE_PAST = 4;
	static final int TENSE_PAST_PROGRESSIVE = 5;
	static final int TENSE_PAST_PERFECT = 6;
	static final int TENSE_PAST_PERFECT_PROGRESSIVE = 7;
	static final int TENSE_FUTURE = 8;
	static final int TENSE_FUTURE_PROGRESSIVE = 9;
	static final int TENSE_FUTURE_PERFECT = 10;
	static final int TENSE_FUTURE_PERFECT_PROGRESSIVE = 11;
	static final int TENSE_LENGTH = 12;
	static final int JOIN_SIMPLE = 0;
	static final int JOIN_COMPOUND = 1;
	static final int JOIN_COMPLEX = 2;
	static final int JOIN_LENGTH = 3;
	static final int SINGULAR = 0;
	static final int PLURAL = 1;
	static final int REQ = 0;
	static final int RES = 1;
	static final int TARGET = 0;
	static final int REPLACEMENT = 1;
	static final String TYPE_POSITIVE = "positive";
	static final String TYPE_NEGATIVE = "negative";
	static final String TYPE_NONE = "";
	static final String splitStr = " : ";
	static final String patnSplitStr = " ";
	static final String argSplitStr = " , ";
	static final String joinSplitStr = "...";

	Random random = new Random();

	ArrayList<String[]> verbs;
	ArrayList<String[]> subjs;
	ArrayList<String[]> objs;
	ArrayList<String[]> adjs;
	ArrayList<String[]> advs;
	ArrayList<String[]> nouns;
	ArrayList<String[]>[][] sentencePatterns;
	ArrayList<File>[][]sentencePatternsFile;
	ArrayList<String[]>[] sentenceJoinPatterns;
	ArrayList<String[]>[] abstracts;
	ArrayList<String>[] abstractsFileName;
	ArrayList<String>[] decodeRepls;
	ArrayList<String>[] encodeRepls;
	ArrayList<String>[] nounSuffixes;
	ArrayList<String>[][] verbSuffixes;

	String[] typeStrs;
	String[] vowels;
	String[] demons;
	String[] whWords;
	String[] simpleTenseVerbs;
	String[] perfectTenseVerbs;
	String[] tenseVerbMods;
	String[] verbModVerbs;
	String[] prepos;
	char[] noDubs;

	ArrayList<String> userDatas;
	ArrayList<String[]> userDatasArgs;
	ArrayList<File> userDatasFile;
	String[] userDatasFilesName;

	File botDataDir;
	File absDir;
	File verbDir;
	File adjDir;
	File advDir;
	File nounDir;
	File subjDir;
	File objDir;
	File prepoDir;
	File replDir;
	File patnDir;
	File sfxDir;
	File userDataDir;

	public Bot(File dataDirectory)
	{
		this.botDataDir = dataDirectory;

		verbs = new ArrayList<String[]>();
		subjs = new ArrayList<String[]>();
		objs = new ArrayList<String[]>();
		adjs = new ArrayList<String[]>();
		advs = new ArrayList<String[]>();
		nouns = new ArrayList<String[]>();

		sentencePatterns = new ArrayList[2][TYPE_LENGTH];
		sentencePatterns[REQ][TYPE_ASSERTIVE] = new ArrayList<String[]>();
		sentencePatterns[REQ][TYPE_IMPERATIVE] = new ArrayList<String[]>();
		sentencePatterns[REQ][TYPE_INTERROGATIVE] = new ArrayList<String[]>();
		sentencePatterns[REQ][TYPE_EXCLAMATORY] = new ArrayList<String[]>();
		sentencePatterns[RES][TYPE_ASSERTIVE] = new ArrayList<String[]>();
		sentencePatterns[RES][TYPE_IMPERATIVE] = new ArrayList<String[]>();
		sentencePatterns[RES][TYPE_INTERROGATIVE] = new ArrayList<String[]>();
		sentencePatterns[RES][TYPE_EXCLAMATORY] = new ArrayList<String[]>();

		sentencePatternsFile = new ArrayList[2][TYPE_LENGTH];
		sentencePatternsFile[REQ][TYPE_ASSERTIVE] = new ArrayList<File>();
		sentencePatternsFile[REQ][TYPE_IMPERATIVE] = new ArrayList<File>();
		sentencePatternsFile[REQ][TYPE_INTERROGATIVE] = new ArrayList<File>();
		sentencePatternsFile[REQ][TYPE_EXCLAMATORY] = new ArrayList<File>();
		sentencePatternsFile[RES][TYPE_ASSERTIVE] = new ArrayList<File>();
		sentencePatternsFile[RES][TYPE_IMPERATIVE] = new ArrayList<File>();
		sentencePatternsFile[RES][TYPE_INTERROGATIVE] = new ArrayList<File>();
		sentencePatternsFile[RES][TYPE_EXCLAMATORY] = new ArrayList<File>();

		sentenceJoinPatterns = new ArrayList[JOIN_LENGTH];
		sentenceJoinPatterns[JOIN_SIMPLE] = new ArrayList<String[]>();
		sentenceJoinPatterns[JOIN_COMPOUND] = new ArrayList<String[]>();
		sentenceJoinPatterns[JOIN_COMPLEX] = new ArrayList<String[]>();

		abstracts = new ArrayList[2];
		abstracts[REQ] = new ArrayList<String[]>();
		abstracts[RES] = new ArrayList<String[]>();

		abstractsFileName = new ArrayList[2];
		abstractsFileName[REQ] = new ArrayList<String>();
		abstractsFileName[RES] = new ArrayList<String>();

		decodeRepls = new ArrayList[2];
		decodeRepls[TARGET] = new ArrayList<String>();
		decodeRepls[REPLACEMENT] = new ArrayList<String>();
		encodeRepls = new ArrayList[2];
		encodeRepls[TARGET] = new ArrayList<String>();
		encodeRepls[REPLACEMENT] = new ArrayList<String>();

		nounSuffixes = new ArrayList[2];
		nounSuffixes[SINGULAR] = new ArrayList<String>();
		nounSuffixes[PLURAL] = new ArrayList<String>();

		verbSuffixes = new ArrayList[2][12];
		verbSuffixes[SINGULAR] = new ArrayList[12];
		verbSuffixes[PLURAL] = new ArrayList[12];

		for(int tense = 0;tense < 12;tense++)
		{
			verbSuffixes[SINGULAR][tense] = new ArrayList<String>();
			verbSuffixes[PLURAL][tense] = new ArrayList<String>();
		}

		typeStrs = new String[]{
			"assertive",
			"imperative",
			"interrogative",
			"exclamatory"
		};

		vowels =  new String[]{"a", "e", "i", "o", "u"};

		noDubs = new char[]{'h', 'w', 'x', 'y'};

		demons = new String[]{
			"a",
			"an",
			"the",
			"this",
			"that",
			"these",
			"those"
		};

		whWords = new String[]{
			"what",
			"why",
			"how",
			"who",
			"whom",
			"where",
			"when"
		};

		//simple present, past and future tense verbs
		simpleTenseVerbs = new String[]{
			"is",
			"am",
			"are",
			"was",
			"were",
			"will",
			"shall",
			"would",
			"can",
			"could"
		};

		//perfect tense verbs
		perfectTenseVerbs = new String[]{
			"has",
			"have",
			"had"
		};

		//perfect progressive or simple progressive verb modifiers
		tenseVerbMods = new String[]{
			"be",
			"being",
			"been"
		};

		//verb modifiers
		verbModVerbs = new String[]{
			"do",
			"does",
			"did"
		};

		nouns.add(new String[]{
					  "i",
					  "we",
					  "you",
					  "he",
					  "she",
					  "it",
					  "they"
				  });

		nouns.add(new String[]{
					  "me",
					  "us",
					  "you",
					  "her",
					  "him",
					  "it",
					  "them"
				  });

		nouns.add(new String[]{
					  "mine",
					  "yours",
					  "ours",
					  "theirs",
					  "his",
					  "hers"
				  });

		adjs.add(new String[]{
					 "my",
					 "your",
					 "our",
					 "their",
					 "his",
					 "her"
				 });

		userDatas = new ArrayList<String>();
		userDatasArgs = new ArrayList<String[]>();
		userDatasFile = new ArrayList<File>();
		userDatasFilesName = new String[whWords.length + simpleTenseVerbs.length + perfectTenseVerbs.length + verbModVerbs.length + 1];

		for(int q = 0;q < whWords.length;q++)
			userDatasFilesName[q] = whWords[q];

		for(int q = 0;q < simpleTenseVerbs.length;q++)
			userDatasFilesName[whWords.length + q] = simpleTenseVerbs[q];

		for(int q = 0;q < perfectTenseVerbs.length;q++)
			userDatasFilesName[whWords.length + simpleTenseVerbs.length + q] = perfectTenseVerbs[q];

		for(int q = 0;q < verbModVerbs.length;q++)
			userDatasFilesName[whWords.length + simpleTenseVerbs.length + verbModVerbs.length + q] = verbModVerbs[q];

		userDatasFilesName[userDatasFilesName.length-1] = "other";

		absDir = new File(botDataDir, "abstracts");
		verbDir = new File(botDataDir, "verbs");
		adjDir = new File(botDataDir, "adjectives");
		advDir = new File(botDataDir, "adverbs");
		nounDir = new File(botDataDir, "nouns");
		subjDir = new File(botDataDir, "subjects");
		objDir = new File(botDataDir, "objects");
		prepoDir = new File(botDataDir, "prepositions");
		replDir = new File(botDataDir, "replacements");
		patnDir = new File(botDataDir, "patterns");
		sfxDir = new File(botDataDir, "suffixes");
		userDataDir = new File(botDataDir, "userData");

		if(!absDir.exists())
			absDir.mkdir();
		if(!verbDir.exists())
			verbDir.mkdir();
		if(!adjDir.exists())
			adjDir.mkdir();
		/*if(!subjDir.exists())
		 subjDir.mkdir();
		 if(!objDir.exists())
		 objDir.mkdir();*/
		if(!replDir.exists())
			replDir.mkdir();
		if(!advDir.exists())
			advDir.mkdir();
		if(!nounDir.exists())
			nounDir.mkdir();
		if(!prepoDir.exists())
			prepoDir.mkdir();
		if(!patnDir.exists())
			patnDir.mkdir();
		if(!sfxDir.exists())
			sfxDir.mkdir();
		if(!userDataDir.exists())
			userDataDir.mkdir();

		for(String q : userDatasFilesName)
		{
			File file = new File(userDataDir, q + ".udata");
			try
			{
				if(!file.exists())
				{
					file.createNewFile();
					userDatasArgs.add(new String[]{"","","","","","","","","",""});
					userDatas.add("");
				}
				else
				{
					String[] lines = readFromFile(file, "SEPARATOR_NEW_LINE");
					for(String line : lines)
					{
						line = line.trim();
						String[] arg_Data = line.split(splitStr);
						userDatasArgs.add(arg_Data[REQ].split("[" + argSplitStr + "]" + "+"));
						userDatas.add(arg_Data[RES]);
					}
				}

				userDatasFile.add(file);
			}catch (IOException e)
			{}
		}

		try
		{
			File prepoFile = new File(prepoDir, "default.prepo");
			if(prepoFile.exists())
				prepos = readFromFile(prepoFile, "SEPARATOR_NEW_LINE");

			File[] botDataDirFiles = botDataDir.listFiles();
			for(File folder : botDataDirFiles)
			{
				File[] files = folder.listFiles();

				for(int i = 0;i < files.length;i++)
				{
					File file = files[i];

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
							}
						}

						else

						//simple sentence parts extraction
						if(file.getName().endsWith(".verb"))
						{
							verbs.add(readFromFile(file, "SEPARATOR_NEW_LINE"));
						}

						else

						if(file.getName().endsWith(".adj"))
						{
							adjs.add(readFromFile(file, "SEPARATOR_NEW_LINE"));
						}

						else

						if(file.getName().endsWith(".adv"))
						{
							advs.add(readFromFile(file, "SEPARATOR_NEW_LINE"));
						}

						else

						if(file.getName().endsWith(".noun"))
						{
							nouns.add(readFromFile(file, "SEPARATOR_NEW_LINE"));
						}

						else

						if(file.getName().endsWith(".subj"))
						{
							subjs.add(readFromFile(file, "SEPARATOR_NEW_LINE"));
						}

						else

						if(file.getName().endsWith(".obj"))
						{
							objs.add(readFromFile(file, "SEPARATOR_NEW_LINE"));
						}

						else

						if(file.getName().endsWith(".repl"))
						{
							boolean isDecode = file.getName().contains("decode");
							boolean isEncode = file.getName().contains("encode");

							String[] replsSplit = readFromFile(file, "SEPARATOR_NEW_LINE");

							for(String replS : replsSplit)
							{
								String[] replSplit = replS.split(splitStr, 2);
								String tgt = replSplit[TARGET];
								String repl = replSplit[REPLACEMENT];

								if(isDecode)
								{
									decodeRepls[TARGET].add(tgt);
									decodeRepls[REPLACEMENT].add(repl);
								}

								else

								if(isEncode)
								{
									encodeRepls[TARGET].add(tgt);
									encodeRepls[REPLACEMENT].add(repl);
								}
							}
						}

						else

						if(file.getName().endsWith(".nsfx"))
						{
							String[] sfxLines = readFromFile(file, "SEPARATOR_NEW_LINE");

							for(String sfxLine : sfxLines)
							{
								String[] sfxSplit = sfxLine.split(splitStr, 2);
								String sfxSing = sfxSplit[SINGULAR];
								String sfxPlu = sfxSplit[PLURAL];

								nounSuffixes[SINGULAR].add(sfxSing);
								nounSuffixes[PLURAL].add(sfxPlu);
							}
						}

						else

						if(file.getName().endsWith(".vsfx"))
						{
							String[] sfxLines = readFromFile(file, "SEPARATOR_NEW_LINE");

							for(String sfxLine : sfxLines)
							{
								String[] sfxSplit = sfxLine.split(splitStr, 2);
								String sfxSing = sfxSplit[SINGULAR];
								String sfxPlu = sfxSplit[PLURAL];

								if(file.getName().contains("presentSimple"))
								{
									verbSuffixes[SINGULAR][TENSE_PRESENT].add(sfxSing);
									verbSuffixes[PLURAL][TENSE_PRESENT].add(sfxPlu);
								}
								else
								if(file.getName().contains("pastSimple"))
								{
									verbSuffixes[SINGULAR][TENSE_PAST].add(sfxSing);
									verbSuffixes[PLURAL][TENSE_PAST].add(sfxPlu);
								}
								else
								if(file.getName().contains("presentProgressive"))
								{
									verbSuffixes[SINGULAR][TENSE_PRESENT_PROGRESSIVE].add(sfxSing);
									verbSuffixes[PLURAL][TENSE_PRESENT_PROGRESSIVE].add(sfxPlu);
								}
								else
								if(file.getName().contains("presentPerfect"))
								{
									verbSuffixes[SINGULAR][TENSE_PRESENT_PERFECT].add(sfxSing);
									verbSuffixes[PLURAL][TENSE_PRESENT_PERFECT].add(sfxPlu);
								}
							}
						}
					}
					else
					{
						if(folder.getName().equals("patterns"))
						{
							String patnFolderName = file.getName();
							File[] patnFiles = file.listFiles();
							Arrays.sort(patnFiles);

							for(File patnFile : patnFiles)
							{
								if(patnFile.getName().endsWith(".spatn"))
								{
									if(patnFile.getName().replace(".spatn", "").endsWith(".req"))
									{
										if(patnFolderName.equals("assertive"))
										{//for the sake of arranged indices, there should be three other tense alternatives for an assertive patn or atleast an empty string array
											sentencePatterns[REQ][TYPE_ASSERTIVE].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
											sentencePatternsFile[REQ][TYPE_ASSERTIVE].add(patnFile);

											if((patnFile = new File(folder, "imperative" + folder.separator + patnFile.getName())).exists())
											{
												sentencePatterns[REQ][TYPE_IMPERATIVE].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
												sentencePatternsFile[REQ][TYPE_IMPERATIVE].add(patnFile);
											}
											else
											{
												patnFile.createNewFile();
												sentencePatterns[REQ][TYPE_IMPERATIVE].add(new String[]{});
												sentencePatternsFile[REQ][TYPE_IMPERATIVE].add(patnFile);
											}

											if((patnFile = new File(folder, "interrogative" + folder.separator + patnFile.getName())).exists())
											{
												sentencePatterns[REQ][TYPE_INTERROGATIVE].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
												sentencePatternsFile[REQ][TYPE_INTERROGATIVE].add(patnFile);
											}
											else
											{
												patnFile.createNewFile();
												sentencePatterns[REQ][TYPE_INTERROGATIVE].add(new String[]{});
												sentencePatternsFile[REQ][TYPE_INTERROGATIVE].add(patnFile);
											}

											if((patnFile = new File(folder, "exclamatory" + folder.separator + patnFile.getName())).exists())
											{
												sentencePatterns[REQ][TYPE_EXCLAMATORY].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
												sentencePatternsFile[REQ][TYPE_EXCLAMATORY].add(patnFile);
											}
											else
											{
												patnFile.createNewFile();
												sentencePatterns[REQ][TYPE_EXCLAMATORY].add(new String[]{});
												sentencePatternsFile[REQ][TYPE_EXCLAMATORY].add(patnFile);
											}
										}
									}

									else

									if(patnFile.getName().replace(".spatn", "").endsWith(".res"))
									{
										if(patnFolderName.equals("assertive"))
										{//for the sake of arranged indices, there should be three other tense alternatives for an assertive patn or atleast an empty string array
											sentencePatterns[RES][TYPE_ASSERTIVE].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
											sentencePatternsFile[RES][TYPE_ASSERTIVE].add(patnFile);

											if((patnFile = new File(folder, "imperative" + folder.separator + patnFile.getName())).exists())
											{
												sentencePatterns[RES][TYPE_IMPERATIVE].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
												sentencePatternsFile[RES][TYPE_IMPERATIVE].add(patnFile);
											}
											else
											{
												sentencePatterns[RES][TYPE_IMPERATIVE].add(new String[]{});
												sentencePatternsFile[RES][TYPE_IMPERATIVE].add(patnFile);
											}

											if((patnFile = new File(folder, "interrogative" + folder.separator + patnFile.getName())).exists())
											{
												sentencePatterns[RES][TYPE_INTERROGATIVE].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
												sentencePatternsFile[RES][TYPE_INTERROGATIVE].add(patnFile);
											}
											else
											{
												sentencePatterns[RES][TYPE_INTERROGATIVE].add(new String[]{});
												sentencePatternsFile[RES][TYPE_INTERROGATIVE].add(patnFile);
											}

											if((patnFile = new File(folder, "exclamatory" + folder.separator + patnFile.getName())).exists())
											{
												sentencePatterns[RES][TYPE_EXCLAMATORY].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
												sentencePatternsFile[RES][TYPE_EXCLAMATORY].add(patnFile);
											}
											else
											{
												sentencePatterns[RES][TYPE_EXCLAMATORY].add(new String[]{});
												sentencePatternsFile[RES][TYPE_EXCLAMATORY].add(patnFile);
											}
										}
									}
								}

								else

								if(patnFile.getName().endsWith(".jpatn"))
								{
									if(patnFile.getName().contains("simple"))
										sentenceJoinPatterns[JOIN_SIMPLE].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
									else
									if(patnFile.getName().contains("compound"))
										sentenceJoinPatterns[JOIN_COMPOUND].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
									else
									if(patnFile.getName().contains("complex"))
										sentenceJoinPatterns[JOIN_COMPLEX].add(readFromFile(patnFile, "SEPARATOR_NEW_LINE"));
								}
							}
						}
					}
				}
			}
		}catch(IOException e)
		{}
	}

	public String[] getReply(String msg)
	{
		String reply = "";
		ArrayList<String> rList = new ArrayList<String>();
		ArrayList<String> lines = new ArrayList<String>();

		boolean hasEnd = false;

		String[] assLines;
		if(msg.contains("."))
		{
			assLines = msg.split("[.]+");
			hasEnd = true;
		}
		else
		{
			assLines = new String[]{msg};
			hasEnd = false;
		}

		for(String assLine : assLines)
		{
			if(hasEnd)
				assLine = assLine + ".";

			if(!assLine.equals(".") 
			   && !assLine.equals(" ")
			   && !assLine.equals(""))
			{
				String[] inLines;
				if(assLine.contains("?"))
				{
					inLines = assLine.split("[?]+");
					hasEnd = true;
				}
				else
				{
					inLines = new String[]{assLine};
					hasEnd = false;
				}

				for(String inLine : inLines)
				{
					if(hasEnd)
						inLine = inLine + "?";

					if(!inLine.equals("?") 
					   && !inLine.equals(" ")
					   && !inLine.equals(""))
					{
						String[] exLines;
						if(inLine.contains("!"))
						{
							exLines = inLine.split("[!]+");
							hasEnd = true;
						}
						else
						{
							exLines = new String[]{inLine};
							hasEnd = false;
						}
						for(String exLine : exLines)
						{
							if(hasEnd)
								exLine = exLine + "!";

							if(!exLine.equals("!") 
							   && !exLine.equals(" ")
							   && !exLine.equals(""))
							{
								String[] semiLines;
								if(exLine.contains(";"))
								{
									semiLines = exLine.split(";");
									hasEnd = true;
								}
								else
								{
									semiLines = new String[]{exLine};
									hasEnd = false;
								}
								for(String semiLine : semiLines)
								{
									if(hasEnd)
										semiLine = semiLine + ";";

									if(!semiLine.equals(";") 
									   && !semiLine.equals(" ")
									   && !semiLine.equals(""))
									{
										String line = semiLine.trim().toLowerCase();
										boolean matches = false;

										//checking for more than one clause in a sentence which are(or can be made) independent
										for(int jn = 0;jn < JOIN_LENGTH;jn++)
										{
											for(int iPatns = 0;iPatns < sentenceJoinPatterns[jn].size();iPatns++)
											{
												for(int iPatn = 0;iPatn < sentenceJoinPatterns[jn].get(iPatns).length;iPatn++)
												{
													String pattern = sentenceJoinPatterns[jn].get(iPatns)[iPatn];
													if(pattern.startsWith(joinSplitStr))
														pattern = " " + pattern;
													if(pattern.endsWith(joinSplitStr))
														pattern = pattern + " ";
													String[] patternParts = pattern.split("[" + joinSplitStr + "]+");
													int[] patternPartsIndexes = new int [patternParts.length];
													for(int ippi = 0;ippi < patternPartsIndexes.length;ippi++)
														patternPartsIndexes[ippi] = -1;

													for(int ipp = 0;ipp < patternParts.length;ipp++)
													{
														matches = false;

														String patternPart = patternParts[ipp];

														if(patternPart.equals(" "))
														{
															if(ipp == 0)
																patternPartsIndexes[ipp] = 0;
															else
															if(ipp == patternParts.length-1)
																patternPartsIndexes[ipp] = line.length()-1;

															matches = true;
														}
														else
														if(line.contains(patternPart))
														{
															patternPartsIndexes[ipp] = line.indexOf(patternPart);
															matches = true;
														}
														else
															break;
													}

													if(matches)
													{
														for(int ippi = 0;ippi < patternPartsIndexes.length;ippi++)
														{
															for(int itippi = 0;itippi < ippi;itippi++)
															{
																matches = false;

																if((patternPartsIndexes[itippi] < patternPartsIndexes[ippi]
																   && patternPartsIndexes[itippi] != -1))
																	matches = true;
																else
																	break;
															}
														}

														if(matches)
														{
															//break sentences
															//simple and compound
															if(jn == JOIN_COMPOUND || jn == JOIN_SIMPLE)
															{
																for(int ippi = 0;ippi < patternPartsIndexes.length-1;ippi++)
																{
																	if(!patternParts[ippi].equals(" "))
																	{
																		line = line.replace(patternParts[ippi], "");

																		for(int ifippi = ippi+1;ifippi < patternPartsIndexes.length;ifippi++)
																			patternPartsIndexes[ifippi] -= patternParts[ippi].length();
																	}

																	if(!patternParts[ippi+1].equals(" ") && !patternParts[ippi].equals(" "))
																	{
																		lines.add(line.substring(patternPartsIndexes[ippi], patternPartsIndexes[ippi+1]));
																	}
																	else
																	{
																		if(ippi == 0)
																			lines.add(line.substring(0, patternPartsIndexes[ippi+1]));
																		else
																		if(ippi == patternPartsIndexes.length-2)
																			lines.add(line.substring(patternPartsIndexes[ippi], line.length()));
																	}
																} 
															}
														}
													}
													if(matches)
														break;
												}
											}
										}
										if(!matches)
											lines.add(semiLine);
									}
								}
							}
						}
					}
				}
			}
		}

		for(String line : lines)
		{try{
				line = line.trim().toLowerCase();
				boolean isAbs = false;

				for(int i = 0;i < abstracts[REQ].size();i++)
				{
					String[] reqGroup = abstracts[REQ].get(i);

					for(int j = 0;j < reqGroup.length;j++)
					{
						String req = reqGroup[j];

						if(line.trim().toLowerCase().equals(req.trim().toLowerCase()))
						{
							String[] resGroup = abstracts[RES].get(i);
							int iRes = random.nextInt(resGroup.length);
							String res = resGroup[iRes];

							if(reply.trim().equals(""))
								reply = res;
							else
								reply += "\n" + res;

							isAbs = true;
							break;
						}
					}
					if(isAbs)
						break;
				}

				if(!isAbs)
				{
					String linesArgs = "";
					String[] wordArray = line.split("[ ]+");
					ArrayList<String> words = new ArrayList<String>();

					for(int aw = 0;aw < wordArray.length;aw++)
					{
						String word = wordArray[aw];

						if(!word.equals(""))
						{
							boolean isSimple = true;

							for(int i = 0;i < decodeRepls[TARGET].size();i++)
							{
								String tgt = decodeRepls[TARGET].get(i);

								if(tgt.contains("getWord()"))
									tgt = tgt.replace("getWord()", word.replace(tgt.replace("getWord()", ""), ""));

								if(word.equals(tgt))
								{
									String repl = decodeRepls[REPLACEMENT].get(i);
									if(repl.contains("getWord()"))
										repl = repl.replace("getWord()", word.replace(decodeRepls[TARGET].get(i).replace("getWord()", ""), ""));

									word = repl;

									isSimple = false;

									if(aw+1 < words.size()) 
									{
										if(word.endsWith("is"))
										{
											boolean is = false;
											String wordWithoutS = word.replace(" is", "");
											if(wordWithoutS.equals("he") || wordWithoutS.equals("she"))
												is = true;
											else
												for(String demon : demons)
													if(wordArray[aw+1].equals(demon))
														is = true;

											/*if(!is)
											 {
											 if(listOfArrayContains(nouns, wordArray[aw+1]) && !listOfArrayContains(adjs, wordArray[aw+1]))
											 {
											 word = wordArray[aw+1] + " of " + wordWithoutS;
											 wordArray[aw+1] = "";
											 }
											 else
											 {
											 if(listOfArrayContains(adjs, wordArray[aw+1]) && wordArray.length > aw+2 && listOfArrayContains(nouns, wordArray[aw+2]))
											 {
											 word = wordArray[aw+1] + wordArray[aw+2] + " of " + wordWithoutS;
											 wordArray[aw+1] = "";
											 wordArray[aw+2] = "";
											 }
											 else
											 is = true;
											 }
											 }*/
										}
									}
								}
							}

							if(isSimple)
								words.add(word);
							else
							if(word.contains(" "))
							{
								String[] replWords = word.split("[ ]+");
								for(String replWord : replWords)
								{
									if(!replWord.equals(""))
										words.add(replWord);
								}
							}
							else
							if(!word.equals(""))
								words.add(word);
						}
					}

					int lineType = TYPE_ABSTRACT;

					//simple classification
					if(line.endsWith("."))
						lineType = TYPE_ASSERTIVE;
					else
					if(line.endsWith("!"))
						lineType = TYPE_EXCLAMATORY;
					else
					if(line.endsWith("?"))
						lineType = TYPE_INTERROGATIVE;

					//starts with a question word?
					String lineQWord = "";
					boolean startsWithQ = false;

					for(String whWord : whWords)
						if(words.get(0).equals(whWord))
						{
							startsWithQ = true;
							lineQWord = whWord;
						}

					if(!startsWithQ)
						for(String toBeVerb : simpleTenseVerbs)
							if(words.get(0).equals(toBeVerb))
							{
								startsWithQ = true;
								lineQWord = toBeVerb;
							}

					if(!startsWithQ)
						for(String toBeVerb : perfectTenseVerbs)
							if(words.get(0).equals(toBeVerb))
							{
								startsWithQ = true;
								lineQWord = toBeVerb;
							}

					if(!startsWithQ)
						for(String modVerb : verbModVerbs)
							if(words.get(0).equals(modVerb))
							{
								startsWithQ = true;
								lineQWord = modVerb;
							}

					//advanced classification

					String lineVerb = "";
					String lineSubj = "";
					String lineObj = "";

					String lineAdj = "";
					String lineAdv = "";
					String lineObjAdj = "";

					String lineSubjDemon = "";
					String lineObjDemon = "";

					String lineMod = "";

					String lineVerbModSuffix = "";

					String lineSubjGroupSfx = "";
					String lineObjGroupSfx = "";

					int lineSubjGroup = SINGULAR;
					int lineObjGroup = SINGULAR;

					int lineVerbIndex = -1;
					int lineSubjIndex = -1;
					int lineObjIndex = -1;

					int lineAdjIndex = -1;
					int lineAdvIndex = -1;
					int lineObjAdjIndex = -1;

					int lineSubjDemonIndex = -1;
					int lineObjDemonIndex = -1;

					int lineTense = TENSE_PRESENT;

					String lineClause = "";

					String[] lineArgs = new String[]{};
					String lineArgsInternal = "";

					for(int w = startsWithQ? 1 : 0;w < words.size();w++)
					{
						boolean isMod = false;
						boolean isPrepo = false;

						for(String tbv : simpleTenseVerbs)
							if(words.get(w).equals(tbv))
								isMod = true;

						for(String tbv2 : perfectTenseVerbs)
							if(words.get(w).equals(tbv2))
								isMod = true;

						for(String tbv3 : tenseVerbMods)
							if(words.get(w).equals(tbv3))
								isMod = true;

						for(String verbTenseMod : verbModVerbs)
							if(words.get(w).equals(verbTenseMod))
								isMod = true;


						for(String prepo : prepos)
							if(words.get(w).equals(prepo))
							{
								isPrepo = true;
							}else
							{
								if(prepo.trim().contains(" "))
								{
									String[] prepoParts = prepo.split("[ ]+");
									if(w+prepoParts.length <= words.size())
									{
										isPrepo = true;

										for(int pp = 0;pp < prepoParts.length;pp++)
										{
											String prepoPart = prepoParts[pp];

											if(!prepoPart.trim().equalsIgnoreCase(words.get(w+pp)))
											{
												isPrepo = false;
												break;
											}
										}
									}
								}
							}

						if(!isMod && !isPrepo)
						{
							String word = words.get(w).trim();
							if(w == words.size()-1)
								word = word.replace(".", "");

							if(!word.equals(""))
							{
								//finding simple verb
								if(listOfArrayContains(verbs, word) 
								   && !listOfArrayContains(nouns, word))
								{
									lineVerb = word;
									lineVerbIndex = w;
								}
								else
								{
									//finding complex verbs and simple tense
									String newWord;

									if(word.endsWith("s")
									   && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-1))
									   && !listOfArrayContains(nouns, newWord))
									{
										lineVerb = newWord;
										lineVerbIndex = w;
										lineTense = TENSE_PRESENT;
										lineVerbModSuffix = "s";
									}
									else
									{
										if(word.endsWith("es"))
										{
											if(listOfArrayContains(verbs, newWord = word.substring(0, word.length()-2))
											   && !listOfArrayContains(nouns, newWord))
											{
												lineVerb = newWord;
												lineVerbIndex = w;
												lineTense = TENSE_PRESENT;
												lineVerbModSuffix = "es";
											}
											else
											{
												if(word.length() >= 4 && word.charAt(word.length()-3) == word.charAt(word.length()-4))
												{
													boolean dub = true;

													for(char nD : noDubs)
														if(nD == word.charAt(word.length()-3))
															dub = false;

													//consonant-vowel-consonant rule
													if((dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-3)))
													   || (!dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-2))))
													{ 
														if(!listOfArrayContains(nouns, newWord)) 
														{
															lineVerb = newWord;
															lineVerbIndex = w;
															lineTense = TENSE_PRESENT;
															lineVerbModSuffix = word.replace(newWord, ""); 
														}
													}
												}
											}
										}
										else
										{
											if(word.endsWith("ed") || word.endsWith("en"))
											{
												if(listOfArrayContains(verbs, newWord = word.substring(0, word.length()-2)) 
												   && !listOfArrayContains(nouns, newWord))
												{
													lineVerb = newWord;
													lineVerbIndex = w;
													lineTense = TENSE_PAST;
													lineVerbModSuffix = word.endsWith("ed")? "ed" : "en";
												}
												else
												{
													//consonant-vowel-consonant rule
													if(word.length() >= 4 && word.charAt(word.length()-3) == word.charAt(word.length()-4))
													{
														boolean dub = true;

														for(char nD : noDubs)
															if(nD == word.charAt(word.length()-3))
																dub = false;
														if((dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-3)))
														   || (!dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-2))))
														{ 
															if(!listOfArrayContains(nouns, newWord)) 
															{
																lineVerb = newWord;
																lineVerbIndex = w;
																lineTense = TENSE_PAST;
																lineVerbModSuffix = word.replace(newWord, ""); 
															}
														}
													}
												}
											}
											else
											if(word.endsWith("ing"))
											{
												if(listOfArrayContains(verbs, newWord = word.substring(0, word.length()-3)) 
												   && !listOfArrayContains(nouns, newWord))
												{
													lineVerb = newWord;
													lineVerbIndex = w;
													lineTense = TENSE_PRESENT_PROGRESSIVE;
													lineVerbModSuffix = "ing";
												}
												else
												{
													//consonant-vowel-consonant rule
													if(word.length() >= 5 && word.charAt(word.length()-4) == word.charAt(word.length()-5))
													{
														boolean dub = true;

														for(char nD : noDubs)
															if(nD == word.charAt(word.length()-4))
																dub = false;

														if((dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-4)))
														   || (!dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-3))))
														{ 
															if(!listOfArrayContains(nouns, newWord)) 
															{
																lineVerb = newWord;
																lineVerbIndex = w;
																lineTense = TENSE_PRESENT_PROGRESSIVE;
																lineVerbModSuffix = word.replace(newWord, ""); 
															}
														}
													}
												}
											}
										}
									}
									//some irregular suffixes to check
									for(int tense = 0;tense < verbSuffixes[PLURAL].length;tense++) 
									{
										ArrayList<String> pSuffixes = verbSuffixes[PLURAL][tense];

										for(int sp = 0;sp < pSuffixes.size();sp++)
										{
											String pSuffix = pSuffixes.get(sp);

											if((word.endsWith(pSuffix)
											   && word.length() >= pSuffix.length() && listOfArrayContains(verbs, newWord = word.substring(0, word.length() - pSuffix.length()) + verbSuffixes[SINGULAR][tense].get(sp)))
											   || (word.equals(pSuffix)
											   && listOfArrayContains(verbs, newWord = verbSuffixes[SINGULAR][tense].get(sp))))
											{ 
												if(!listOfArrayContains(nouns, newWord))
												{
													lineVerb = newWord;
													lineVerbIndex = w;
													lineTense = tense;
													lineVerbModSuffix = pSuffix; 
												}
											}
										}
									}
								}
							}
						} 
						if(!lineVerb.equals(""))
							break;
					} 

					if(lineVerb.trim().equals("")) 
					{
						for(int w = startsWithQ? 1 : 0;w < words.size();w++)
						{
							boolean isMod = false;
							boolean isPrepo = false;

							for(String tbv : simpleTenseVerbs)
								if(words.get(w).equals(tbv))
									isMod = true;

							for(String tbv2 : perfectTenseVerbs)
								if(words.get(w).equals(tbv2))
									isMod = true;

							for(String tbv3 : tenseVerbMods)
								if(words.get(w).equals(tbv3))
									isMod = true;

							for(String verbTenseMod : verbModVerbs)
								if(words.get(w).equals(verbTenseMod))
									isMod = true;


							for(String prepo : prepos)
								if(words.get(w).equals(prepo))
								{
									isPrepo = true;
								}else
								{
									if(prepo.trim().contains(" "))
									{
										String[] prepoParts = prepo.split("[ ]+");
										if(w+prepoParts.length <= words.size())
										{
											isPrepo = true;

											for(int pp = 0;pp < prepoParts.length;pp++)
											{
												String prepoPart = prepoParts[pp];

												if(!prepoPart.trim().equalsIgnoreCase(words.get(w+pp)))
												{
													isPrepo = false;
													break;
												}
											}
										}
									}
								}

							if(!isMod && !isPrepo)
							{
								String word = words.get(w).trim();
								if(w == words.size()-1)
									word = word.replace(".", "");

								if(!word.equals(""))
								{
									//finding simple verb
									if(listOfArrayContains(verbs, word))
									{
										lineVerb = word;
										lineVerbIndex = w;
									}
									else
									{
										//finding complex verbs and simple tense
										String newWord;

										if(word.endsWith("s")
										   && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-1)))
										{
											lineVerb = newWord;
											lineVerbIndex = w;
											lineTense = TENSE_PRESENT;
											lineVerbModSuffix = "s";
										}
										else
										{
											if(word.endsWith("es"))
											{
												if(listOfArrayContains(verbs, newWord = word.substring(0, word.length()-2)))
												{
													lineVerb = newWord;
													lineVerbIndex = w;
													lineTense = TENSE_PRESENT;
													lineVerbModSuffix = "es";
												}
												else
												{
													if(word.length() >= 4 && word.charAt(word.length()-3) == word.charAt(word.length()-4))
													{
														boolean dub = true;

														for(char nD : noDubs)
															if(nD == word.charAt(word.length()-3))
																dub = false;

														//consonant-vowel-consonant rule
														if((dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-3)))
														   || (!dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-2))))
														{
															lineVerb = newWord;
															lineVerbIndex = w;
															lineTense = TENSE_PRESENT;
															lineVerbModSuffix = word.replace(newWord, "");
														}
													}
												}
											}
											else
											{
												if(word.endsWith("ed") || word.endsWith("en"))
												{
													if(listOfArrayContains(verbs, newWord = word.substring(0, word.length()-2)))
													{
														lineVerb = newWord;
														lineVerbIndex = w;
														lineTense = TENSE_PAST;
														lineVerbModSuffix = word.endsWith("ed")? "ed" : "en";
													}
													else
													{
														//consonant-vowel-consonant rule
														if(word.length() >= 4 && word.charAt(word.length()-3) == word.charAt(word.length()-4))
														{
															boolean dub = true;

															for(char nD : noDubs)
																if(nD == word.charAt(word.length()-3))
																	dub = false;
															if((dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-3)))
															   || (!dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-2))))
															{
																lineVerb = newWord;
																lineVerbIndex = w;
																lineTense = TENSE_PAST;
																lineVerbModSuffix = word.replace(newWord, "");
															}
														}
													}
												}
												else
												if(word.endsWith("ing"))
												{
													if(listOfArrayContains(verbs, newWord = word.substring(0, word.length()-3)))
													{
														lineVerb = newWord;
														lineVerbIndex = w;
														lineTense = TENSE_PRESENT_PROGRESSIVE;
														lineVerbModSuffix = "ing";
													}
													else
													{
														//consonant-vowel-consonant rule
														if(word.length() >= 5 && word.charAt(word.length()-4) == word.charAt(word.length()-5))
														{
															boolean dub = true;

															for(char nD : noDubs)
																if(nD == word.charAt(word.length()-4))
																	dub = false;

															if((dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-4)))
															   || (!dub && listOfArrayContains(verbs, newWord = word.substring(0, word.length()-3))))
															{
																lineVerb = newWord;
																lineVerbIndex = w;
																lineTense = TENSE_PRESENT_PROGRESSIVE;
																lineVerbModSuffix = word.replace(newWord, "");
															}
														}
													}
												}
											}
										}
										//some irregular suffixes to check
										for(int tense = 0;tense < verbSuffixes[PLURAL].length;tense++) 
										{
											ArrayList<String> pSuffixes = verbSuffixes[PLURAL][tense];

											for(int sp = 0;sp < pSuffixes.size();sp++)
											{
												String pSuffix = pSuffixes.get(sp);

												if((word.endsWith(pSuffix)
												   && word.length() >= pSuffix.length() && listOfArrayContains(verbs, newWord = word.substring(0, word.length() - pSuffix.length()) + verbSuffixes[SINGULAR][tense].get(sp)))
												   || (word.equals(pSuffix)
												   && listOfArrayContains(verbs, newWord = verbSuffixes[SINGULAR][tense].get(sp))))
												{
													lineVerb = newWord;
													lineVerbIndex = w;
													lineTense = tense;
													lineVerbModSuffix = pSuffix;
												}
											}
										}
									}
								}
							} 
							if(!lineVerb.equals(""))
								break;
						}
					}

					if(lineVerbIndex != -1) 
					{ 
						int w = lineVerbIndex; 
						String word = words.get(w);

						if(!lineVerb.equals("")) 
						{
							//finding the specific tense
							ArrayList<String> tmpWords = (ArrayList<String>)words.clone();
							int lineVerbIndexDec = 0;

							for(int t = 0;t < tmpWords.size();t++)
							{
								for(String tbv : simpleTenseVerbs)
									if(tmpWords.get(t).equals(tbv))
										break;

								for(String tbv2 : perfectTenseVerbs)
									if(tmpWords.get(t).equals(tbv2))
										break;

								for(String tbv3 : tenseVerbMods)
									if(tmpWords.get(t).equals(tbv3))
										break;

								for(String verbTenseMod : verbModVerbs)
									if(tmpWords.get(t).equals(verbTenseMod))
										break;

								if(listOfArrayContains(verbs, tmpWords.get(t))
								   && !listOfArrayContains(nouns, tmpWords.get(t)))
									break;

								tmpWords.remove(t);
								if(t < lineVerbIndex)
								{
									lineVerbIndex--;
									lineVerbIndexDec++;
								}
							}

							for(int wtv = 0;wtv < lineVerbIndex;wtv++)
							{
								String wordtv = tmpWords.get(wtv).trim();

								if(wordtv.equals("is") || wordtv.equals("am"))
								{
									lineMod = wordtv;

									if(tmpWords.get(wtv+1).trim().equals("being"))
									{
										lineTense = TENSE_PRESENT_PROGRESSIVE;
										lineMod += " " + tmpWords.get(wtv+1).trim();
									}

									lineSubjGroup = SINGULAR;
								}
								else
								{
									if(wordtv.equals("are"))
									{
										lineMod = wordtv;

										if(tmpWords.get(wtv+1).trim().equals("being"))
										{
											lineTense = TENSE_PRESENT_PROGRESSIVE;
											lineMod += " " + tmpWords.get(wtv+1).trim();
										}

										lineSubjGroup = PLURAL;
									}
									else
									{
										if(wordtv.equals("was") || wordtv.equals("were"))
										{
											lineMod = wordtv;

											if(tmpWords.get(wtv+1).trim().equals("being"))
											{
												lineTense = TENSE_PAST_PROGRESSIVE;
												lineMod += " " + tmpWords.get(wtv+1).trim();
											}

											if(word.equals("were"))
												lineSubjGroup = PLURAL;

											if(lineTense >= TENSE_PRESENT && lineTense <= TENSE_PRESENT_PERFECT) 
											{
												lineTense += TENSE_LENGTH/3;//considering the previous tense to be present
											}
										}
										else
										{
											if((wordtv.equals("will")
											   || wordtv.equals("shall")))
											{
												lineTense = TENSE_FUTURE;
												lineMod = wordtv;
											}
											else
											{
												if(wordtv.equals("be"))
												{ 
													lineTense = TENSE_FUTURE;
													lineMod = wordtv;

													if(wtv-1 > -1 &&
													   (tmpWords.get(wtv-1).trim().equals("will")
													   || tmpWords.get(wtv-1).trim().equals("shall")))
													{
														lineMod = tmpWords.get(wtv-1).trim() + " " + wordtv;

														if(lineTense == TENSE_PRESENT_PROGRESSIVE)
														{
															lineTense = TENSE_FUTURE_PROGRESSIVE;
														}
														else
														{
															if(lineTense == TENSE_PAST || lineTense == TENSE_PRESENT_PERFECT)
															{
																lineTense = TENSE_FUTURE_PERFECT;//ATTENTION
															}
														}
													}
												}
												else
												{
													if(wordtv.equals("did"))
													{
														lineTense = TENSE_PAST;
														lineMod = wordtv;
													}
													else
													{
														if(wordtv.equals("do"))
														{
															lineTense = TENSE_PRESENT;
															lineMod = wordtv;
														}
														else
														{
															if(wordtv.equals("does"))
															{
																lineTense = TENSE_PRESENT;
																lineMod = wordtv;
															}
															else
															{
																if(wordtv.equals("doing"))
																{
																	lineTense = TENSE_PRESENT_PROGRESSIVE;
																	lineMod = wordtv;

																	for(int wtdng = 0;wtdng < wtv;wtdng++)
																	{
																		String wordtdng = tmpWords.get(wtdng).trim();

																		if(wordtdng.equals("is")
																		   || wordtdng.equals("am"))
																		{
																			lineTense = TENSE_PRESENT_PROGRESSIVE;
																			lineSubjGroup = SINGULAR;
																			lineMod = wordtdng + " " + wordtv;
																		}
																		else
																		{
																			if(wordtdng.equals("are"))
																			{
																				lineTense = TENSE_PRESENT_PROGRESSIVE;
																				lineSubjGroup = PLURAL;
																				lineMod = wordtdng + " " + wordtv;
																			}
																			else
																			{
																				if(wordtdng.equals("was"))
																				{
																					lineTense = TENSE_PAST_PROGRESSIVE;
																					lineSubjGroup = SINGULAR;
																					lineMod = wordtdng + " " + wordtv;
																				}
																				else
																				{
																					if(wordtdng.equals("were"))
																					{
																						lineTense = TENSE_PAST_PROGRESSIVE;
																						lineSubjGroup = PLURAL;
																						lineMod = wordtdng + " " + wordtv;
																					}
																				}
																			}
																		}
																	}
																}
																else
																{
																	if(wordtv.equals("has"))
																	{
																		lineMod = wordtv;
																		if(wtv+1 < words.size() && tmpWords.get(wtv+1).trim().equals("been"))
																		{
																			lineTense = TENSE_PRESENT_PERFECT;

																			if(lineTense == TENSE_PRESENT_PROGRESSIVE)
																				lineTense = TENSE_PRESENT_PERFECT_PROGRESSIVE;

																			lineMod += " " + tmpWords.get(wtv+1).trim();
																		}
																		else
																		{
																			if(lineTense == TENSE_PAST || lineTense == TENSE_PRESENT_PERFECT)
																				lineTense = TENSE_PRESENT_PERFECT;
																		}
																		lineSubjGroup = SINGULAR;
																	}
																	else
																	{
																		if(wordtv.equals("have"))
																		{
																			lineMod = wordtv;

																			if(wtv+1 < words.size() && tmpWords.get(wtv+1).trim().equals("been"))
																			{
																				lineTense = TENSE_PRESENT_PERFECT;

																				if(wtv-1 > -1 &&
																				   (tmpWords.get(wtv-1).trim().equals("will")
																				   || tmpWords.get(wtv-1).trim().equals("shall")))
																				{
																					lineTense = TENSE_FUTURE_PERFECT;

																					if(lineTense == TENSE_PRESENT_PROGRESSIVE)
																						lineTense = TENSE_FUTURE_PERFECT_PROGRESSIVE;

																					lineMod = tmpWords.get(wtv-1).trim() + " " + wordtv + " " + tmpWords.get(wtv+1).trim();
																				}
																				else
																				{
																					if(lineTense == TENSE_PRESENT_PROGRESSIVE)
																						lineTense = TENSE_PRESENT_PERFECT_PROGRESSIVE;
																				}
																			}
																			else
																			{
																				if(wtv-1 > -1 &&
																				   (tmpWords.get(wtv-1).trim().equals("will")
																				   || tmpWords.get(wtv-1).trim().equals("shall")))
																				{
																					lineTense = TENSE_FUTURE_PERFECT;

																					if(lineTense == TENSE_PAST || lineTense == TENSE_PRESENT_PERFECT)
																						lineTense = TENSE_FUTURE_PERFECT;

																					lineMod = tmpWords.get(wtv-1).trim() + " " + wordtv;
																				}
																				else
																				{
																					if(lineTense == TENSE_PAST || lineTense == TENSE_PRESENT_PERFECT)
																						lineTense = TENSE_PRESENT_PERFECT;
																				}
																			}
																			lineSubjGroup = PLURAL;
																		}
																		else
																		{
																			if(wordtv.equals("had"))
																			{
																				lineMod = wordtv;

																				if(wtv+1 < words.size() && tmpWords.get(wtv+1).trim().equals("been"))
																				{
																					lineTense = TENSE_PAST_PERFECT;

																					if(lineTense == TENSE_PRESENT_PROGRESSIVE)
																						lineTense = TENSE_PAST_PERFECT_PROGRESSIVE;

																					lineMod = wordtv + " " + tmpWords.get(wtv+1).trim();
																				}
																				else
																				{
																					lineTense = TENSE_PAST_PERFECT;

																					if(lineTense == TENSE_PAST || lineTense == TENSE_PRESENT_PERFECT)
																						lineTense = TENSE_PAST_PERFECT;
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
							lineVerbIndex += lineVerbIndexDec;
							/*if(!lineVerb.equals(""))
							 break;*/
						}
					}

					if(words.size() >= 3)
					{
						if(lineVerb.equals(""))
						{
							for(int w = 0;w < words.size();w++)
							{
								for(String simpleTenseVerb : simpleTenseVerbs)
									if(words.get(w).equals(simpleTenseVerb))
									{
										lineVerb = words.get(w);
										lineVerbIndex = w;

										if(simpleTenseVerb.equals("is")
										   || simpleTenseVerb.equals("am")
										   || simpleTenseVerb.equals("are"))
										{
											lineTense = TENSE_PRESENT;
										}
										else
										if(simpleTenseVerb.equals("was")
										   || simpleTenseVerb.equals("were"))
										{
											lineTense = TENSE_PAST;
										}
										else
										if(simpleTenseVerb.equals("will")
										   || simpleTenseVerb.equals("shall"))
										//also "would" to be added
										{
											lineTense = TENSE_FUTURE;
										}
									}

								if(lineVerb.equals(""))
									for(String perfectTenseVerb : perfectTenseVerbs)
										if(words.get(w).equals(perfectTenseVerb))
										{
											lineVerb = words.get(w);
											lineVerbIndex = w;
											lineTense += 2;//considering the previous tense to be simple present or past or future
										}

								if(lineVerb.equals(""))
									for(String verbModVerb : verbModVerbs)
										if(words.get(w).equals(verbModVerbs))
										{
											lineVerb = words.get(w);
											lineVerbIndex = w;

											if(verbModVerb.equals("did"))
											{
												lineTense = TENSE_PAST;
											}
										}
							}
						}
					}

					if(!lineVerb.equals(""))
					{
						if(words.size() == 3)
						{
							if(!startsWithQ)
							{
								if(lineVerbIndex == 1)
								{
									if(!listOfArrayContains(adjs, words.get(0)))
									{
										if(!listOfArrayContains(advs, words.get(0)))
										{
											lineSubj = words.get(0);
											lineSubjIndex = 0;
										}
										else
										{
											lineAdv = words.get(0);
											lineAdvIndex = 0;
										}
									}
									else
									{
										lineAdj = words.get(0);
										lineAdjIndex = 0;
									}

									if(!listOfArrayContains(adjs, words.get(2)))
									{
										if(listOfArrayContains(advs, words.get(2)))
										{
											lineAdv = words.get(2);
											lineAdvIndex = 2;
										}
										else
										{
											lineObj = words.get(2);
											lineObjIndex = 2;
										}
									}
									else
									{
										if(lineSubj.trim().equals(""))
											lineSubj = words.get(0);

										lineAdj = words.get(2);

										if(words.get(1).equals("is"))
										{
											lineVerb = words.get(1);
											lineVerbIndex = 1;
											lineTense = TENSE_PRESENT;
											lineSubjGroup = SINGULAR;
										}

										else

										if(words.get(1).equals("am"))
										{
											lineVerb = words.get(1);
											lineVerbIndex = 1;
											lineTense = TENSE_PRESENT;
											lineSubj = "i";
											lineSubjGroup = SINGULAR;
										}

										else

										if(words.get(1).equals("are"))
										{
											lineVerb = words.get(1);
											lineVerbIndex = 1;
											lineTense = TENSE_PRESENT;
											lineSubjGroup = PLURAL;
										}

										else

										if(words.get(1).equals("was"))
										{
											lineVerb = words.get(1);
											lineVerbIndex = 1;
											lineTense = TENSE_PAST;
											lineSubjGroup = SINGULAR;
										}

										else

										if(words.get(1).equals("were"))
										{
											lineVerb = words.get(1);
											lineVerbIndex = 1;
											lineTense = TENSE_PAST;
											lineSubjGroup = PLURAL;
										}
									}
								}
								else
								if(lineVerbIndex == 2)
								{
									if(listOfArrayContains(adjs, (words.get(0))))
									{
										if(listOfArrayContains(adjs, (words.get(1))))
										{
											lineAdj = words.get(0) + " " + words.get(1);
											lineAdjIndex = 1;
											lineSubj = "";
											lineSubjIndex = -1;
										}
										else
										{
											lineAdj = words.get(0);
											lineAdjIndex = 0;
											lineSubj = words.get(1);
											lineSubjIndex = 1;

											if(listOfArrayContains(advs, (words.get(1))))
											{
												lineAdv = words.get(1);
												lineAdvIndex = 1;
												lineSubj = "";
												lineSubjIndex = -1;
											}
										}
									}
									else
									if(listOfArrayContains(advs, (words.get(0))))
									{
										if(listOfArrayContains(advs, (words.get(1))))
										{
											lineAdv = words.get(0) + " " + words.get(1);
											lineAdvIndex = 1;
											lineSubj = "";
											lineSubjIndex = -1;
										}
										else
										{
											lineAdv = words.get(0);
											lineAdvIndex = 0;
											lineSubj = words.get(1);
											lineSubjIndex = 1;

											if(listOfArrayContains(adjs, (words.get(1))))
											{
												lineAdj = words.get(1);
												lineAdjIndex = 1;
												lineSubj = "";
												lineSubjIndex = -1;
											}

										}
									}
									else
									if(listOfArrayContains(nouns, words.get(0)) 
									   || (!listOfArrayContains(adjs, words.get(0)) && !listOfArrayContains(advs, words.get(0))))
									{
										if(listOfArrayContains(advs, (words.get(1))))
										{
											lineAdv = words.get(1);
											lineAdvIndex = 1;
											lineSubj = words.get(0);
											lineSubjIndex = 0;
										}
										else
										{
											if(listOfArrayContains(advs, (words.get(1))))
											{
												lineAdv = words.get(1);
												lineAdvIndex = 1;
												lineSubj = "";
												lineSubjIndex = -1;
											}
											else
											{
												lineSubj = words.get(0) + " " + words.get(1);
												lineSubjIndex = 1;
											}
										}
									}
								}
								else
								{
									if(lineVerbIndex == 0)
									{
										if(listOfArrayContains(advs, (words.get(1))))
										{
											lineObjAdj = words.get(1);
											lineObjAdjIndex = 1;
											lineObj = words.get(2);
											lineObjIndex = 2;
										}
										else 
										{
											if(listOfArrayContains(nouns, words.get(1)) 
											   || (!listOfArrayContains(adjs, words.get(1)) && !listOfArrayContains(advs, words.get(1))))
											{
												if(listOfArrayContains(advs, words.get(2)))
												{
													lineObj = words.get(1);
													lineObjIndex = 1;
													lineAdv = words.get(2);
													lineAdvIndex = 2;
												}
												else
												{
													lineObj = words.get(1) + " " + words.get(2);
													lineObjIndex = 2;
												}
											}
										}
									}
								}
							}
							else
							{
								for(String whWord : whWords)
									if(lineQWord.trim().equals(whWord))
									{
										lineSubj = lineQWord;
										lineObj = words.get(2);

										if(words.get(1).equals("is"))
										{
											lineVerb = words.get(1);
											lineVerbIndex = 1;
											lineTense = TENSE_PRESENT;
											lineObj = words.get(2);
											lineSubjGroup = SINGULAR;
										}

										else

										if(words.get(1).equals("am"))
										{
											lineVerb = words.get(1);
											lineVerbIndex = 1;
											lineTense = TENSE_PRESENT;
											lineObj = "i";
											lineSubjGroup = SINGULAR;
										}

										else

										if(words.get(1).equals("are"))
										{
											lineVerb = words.get(1);
											lineVerbIndex = 1;
											lineTense = TENSE_PRESENT;
											lineObj = words.get(2);
											lineSubjGroup = PLURAL;
										}

										else

										if(words.get(1).equals("was"))
										{
											lineVerb = words.get(1);
											lineVerbIndex = 1;
											lineTense = TENSE_PAST;
											lineObj = words.get(2);
											lineSubjGroup = SINGULAR;
										}

										else

										if(words.get(1).equals("were"))
										{
											lineVerb = words.get(1);
											lineVerbIndex = 1;
											lineTense = TENSE_PAST;
											lineObj = words.get(2);
											lineSubjGroup = PLURAL;
										}
									}
									else
									{
										if(lineVerbIndex == 2)
										{
											lineSubj = lineQWord;
											lineObj = words.get(1);
										}
									}
							}
						}
						else

						if(words.size() == 2)
						{
							if(lineVerbIndex == 1)
							{
								if(listOfArrayContains(advs, words.get(0)))
								{
									lineAdv = words.get(0);
									lineAdvIndex = 0;
									lineSubj = "";
									lineSubjIndex = -1;
								}
								else
								{
									lineSubj = words.get(0);
									lineSubjIndex = 0;
								}
							}
							else
							{
								if(lineVerbIndex == 0)
								{
									if(listOfArrayContains(advs, words.get(1)))
									{
										lineAdv = words.get(1);
										lineAdvIndex = 1;
										lineObj = "";
										lineObjIndex = -1;
									}
									else
									{
										lineObj = words.get(1);
										lineObjIndex = 1;
									}
								}
							}
						}
						else
						if(words.size() == 1)
							;
						else
						{
							boolean wasDemon = false;
							int demonPhraseLen = 2;

							for(int w = 0;w < words.size();w++)
							{
								boolean isMod = false;
								boolean isPrepo = false;

								for(String tbv : simpleTenseVerbs)
									if(words.get(w).equals(tbv))
										isMod = true;

								for(String tbv2 : perfectTenseVerbs)
									if(words.get(w).equals(tbv2))
										isMod = true;

								for(String tbv3 : tenseVerbMods)
									if(words.get(w).equals(tbv3))
										isMod = true;

								for(String verbTenseMod : verbModVerbs)
									if(words.get(w).equals(verbTenseMod))
										isMod = true;


								for(String prepo : prepos)
									if(words.get(w).equals(prepo))
									{
										isPrepo = true;
									}else
									{
										if(prepo.contains(" "))
										{
											String[] prepoParts = prepo.split("[ ]+");
											if(w+prepoParts.length <= words.size())
											{
												isPrepo = true;

												for(int pp = 0;pp < prepoParts.length;pp++)
												{
													String prepoPart = prepoParts[pp];

													if(!prepoPart.trim().equalsIgnoreCase(words.get(w+pp)))
													{
														isPrepo = false;
														break;
													}
												}
											}
										}
									}

								if(!isMod && !isPrepo)
								{
									if(w != lineVerbIndex)
									{
										boolean isDemon = false;
										for(String demon : demons)
											if(words.get(w).equals(demon))
												isDemon = true;

										if(wasDemon)
											if(w > demonPhraseLen)
												wasDemon = false;

										if(!isDemon)
										{
											if(!wasDemon)
											{
												if((listOfArrayContains(nouns, words.get(w))
												   && !listOfArrayContains(adjs, words.get(w)))
												   || (!listOfArrayContains(adjs, words.get(w))
												   && !listOfArrayContains(advs, words.get(w))
												   && !listOfArrayContains(verbs, words.get(w))))
												{
													if(w < lineVerbIndex)
													{
														if(lineSubj.equals(""))
														{
															lineSubj = words.get(w);
														}
														else
														{
															lineSubj += " " + words.get(w);
														}

														lineSubjIndex = w;
													}
													else
													{
														if(lineObj.equals(""))
														{
															lineObj = words.get(w);
														}
														else
														{
															lineObj += " " + words.get(w);
														}

														lineObjIndex = w;
													}
												}
											}
										}
										else
										{
											wasDemon = true;
											demonPhraseLen = 0;

											if(w != words.size()-1)
											{
												if(!listOfArrayContains(adjs, words.get(w+1)))
												{
													if((listOfArrayContains(nouns, words.get(w+1))
													   && !listOfArrayContains(adjs, words.get(w+1)))
													   || (!listOfArrayContains(advs, words.get(w+1))
													   && !listOfArrayContains(verbs, words.get(w+1))))
													{
														demonPhraseLen = w+2;

														if(w < lineVerbIndex)
														{
															lineSubjDemon = words.get(w);
															lineSubjDemonIndex = w;
															lineSubj = words.get(w+1);
															lineSubjIndex = w+1;
														}
														else
														{
															lineObjDemon = words.get(w);
															lineObjDemonIndex = w;
															lineObj = words.get(w+1);
															lineObjIndex = w+1;
														}
													}
													else
													{
														demonPhraseLen = 0;
														lineSubj = words.get(w);
													}
												}
												else
												{
													if(w+2 < words.size())
													{
														if((listOfArrayContains(nouns, words.get(w+2))
														   && !listOfArrayContains(adjs, words.get(w+2)))
														   || (!listOfArrayContains(adjs, words.get(w+2))
														   && !listOfArrayContains(advs, words.get(w+2))
														   && !listOfArrayContains(verbs, words.get(w+2))))
														{
															demonPhraseLen = w+3;

															if(w < lineVerbIndex)
															{
																lineAdj = words.get(w+1);
																lineAdjIndex = w+1;
																lineSubjDemon = words.get(w);
																lineSubjDemonIndex = w;
																lineSubj = words.get(w+2);
																lineSubjIndex = w+2;
															}
															else
															{
																lineObjAdj = words.get(w+1);
																lineObjAdjIndex = w+1;
																lineObjDemon = words.get(w);
																lineObjDemonIndex = w;
																lineObj = words.get(w+2);
																lineObjIndex = w+2;
															}
														}
														else
														{
															demonPhraseLen = 0;
															lineSubj = words.get(w);
														}
													}
												}
											}
										}
									}
								}
								else
								{
									if(isPrepo)
									{
										if(!lineVerb.equals("") && !lineSubj.equals("") && !lineObj.equals(""))
										{
											for(int i = w;i < words.size();i++)
												lineClause = words.get(i) + " ";
											lineClause = lineClause.trim();
											line = line.substring(0, line.indexOf((w != 0? " " : "") + words.get(w) + (w < words.size()-1? " " : "")));
											break;
										}
									}
								}
							}

							for(int w = 0;w < words.size();w++)
							{
								if(w != lineVerbIndex)
								{
									if(listOfArrayContains(adjs, words.get(w)))
									{
										if(w < lineSubjIndex)
										{
											lineAdjIndex = w;
											lineAdj = words.get(w);
										}
										else
										{
											if(w > lineSubjIndex)
											{
												lineObjAdj = words.get(w);
												lineObjAdjIndex = w;
											}
										}
									}
									else
									{
										if(listOfArrayContains(advs, words.get(w)))
										{
											lineAdvIndex = w;
											lineAdv = words.get(w);
										}
									}
								}
							}
						}
					}
					else
					{
						//do something with no verb line


						//some tmp adjustment code  
						lineType = TYPE_ASSERTIVE;
						/*if(lineVerb.trim().equals("")) 
						 lineVerb = "void";*/

						boolean wasDemon = false;
						int demonPhraseLen = 2;
						boolean isPrepo = false;

						for(int w = 0;w < words.size();w++)
						{
							for(String prepo : prepos)
								if(words.get(w).equals(prepo))
								{
									isPrepo = true;
								}else
								{
									if(prepo.contains(" "))
									{
										String[] prepoParts = prepo.split("[ ]+");
										if(w+prepoParts.length <= words.size())
										{
											isPrepo = true;

											for(int pp = 0;pp < prepoParts.length;pp++)
											{
												String prepoPart = prepoParts[pp];

												if(!prepoPart.trim().equalsIgnoreCase(words.get(w+pp)))
												{
													isPrepo = false;
													break;
												}
											}
										}
									}
								}

							if(!isPrepo)
							{
								boolean isDemon = false;
								for(String demon : demons)
									if(words.get(w).equals(demon))
										isDemon = true;

								if(wasDemon)
									if(w > demonPhraseLen)
										wasDemon = false;

								if(!isDemon)
								{
									if(!wasDemon)
									{
										if((listOfArrayContains(nouns, words.get(w))
										   && !listOfArrayContains(adjs, words.get(w)))
										   || (!listOfArrayContains(adjs, words.get(w))
										   && !listOfArrayContains(advs, words.get(w))
										   && !listOfArrayContains(verbs, words.get(w))))
										{
											if(lineSubj.equals(""))
											{
												lineSubj = words.get(w);
												lineSubjIndex = w;
											}
											else
											{
												if(lineObjAdjIndex != w)
												{
													lineObjIndex = w;
													lineObj = words.get(w);
												}
											}
										}
										else
										{
											if(listOfArrayContains(adjs, words.get(w)))
											{
												if(lineSubj.equals(""))
												{
													lineAdjIndex = w;
													lineAdj = words.get(w);
												}
												else
												{
													if(lineObj.equals(""))
													{
														lineObjAdj = words.get(w);
														lineObjAdjIndex = w;
													}
												}
											}
											else
											{
												if(listOfArrayContains(advs, words.get(w)))
												{
													lineAdvIndex = w; 
													lineAdv = words.get(w);
												}
											}
										}
									}
								}
								else
								{
									wasDemon = true;

									if(w != words.size()-1)
									{
										if(!listOfArrayContains(adjs, words.get(w+1)))
										{
											if((listOfArrayContains(nouns, words.get(w+1))
											   && !listOfArrayContains(adjs, words.get(w+1)))
											   || (!listOfArrayContains(advs, words.get(w+1))
											   && !listOfArrayContains(verbs, words.get(w+1))))
											{
												demonPhraseLen = w+2;

												if(lineSubj.equals(""))
												{
													lineSubjDemon = words.get(w);
													lineSubjDemonIndex = w;
													lineSubj = words.get(w+1);
													lineSubjIndex = w+1;
												}
												else
												{
													lineObjDemon = words.get(w);
													lineObjDemonIndex = w;
													lineObj = words.get(w+1);
													lineObjIndex = w+1;
												}
											}
											else
											{
												demonPhraseLen = 0;
												lineSubj = words.get(w);
											}
										}
										else
										{
											if(w+2 < words.size())
											{
												if((listOfArrayContains(nouns, words.get(w+2))
												   && !listOfArrayContains(adjs, words.get(w+2)))
												   || (!listOfArrayContains(adjs, words.get(w+2))
												   && !listOfArrayContains(advs, words.get(w+2))
												   && !listOfArrayContains(verbs, words.get(w+2))))
												{
													demonPhraseLen = w+3;

													if(lineSubj.equals(""))
													{
														lineSubjDemon = words.get(w);
														lineSubjDemonIndex = w;
														lineAdj = words.get(w+1);
														lineAdjIndex = w+1;
														lineSubj = words.get(w+2);
														lineSubjIndex = w+2;
													}
													else
													{
														lineObjDemon = words.get(w);
														lineObjDemonIndex = w;
														lineObjAdj = words.get(w+1);
														lineObjAdjIndex = w+1;
														lineObj = words.get(w+2);
														lineObjIndex = w+2;
													}
												}
												else
												{
													demonPhraseLen = 0;
													lineSubj = words.get(w);
												}
											}
										}
									}
								}
							}
							else
							{
								if(isPrepo)
								{
									if(!lineSubj.equals("") && !lineObj.equals(""))
									{
										for(int i = w;i < words.size();i++)
											lineClause = words.get(i) + " ";
										lineClause = lineClause.trim();
										line = line.substring(0, line.indexOf((w != 0? " " : "") + words.get(w) + (w < words.size()-1? " " : "")));
										break;
									}
								}
							}
						}
					}

					//some adjustments to be made

					//find whether the subject/object is singular or plural
					if(lineSubj.contains(" "))
					{//subject with multiple words
						String[] lineSubjParts = lineSubj.split("[ ]+");
						String newWord;

						for(String lineSubjPart : lineSubjParts)
						{
							if(!listOfArrayContains(nouns, lineSubjPart))
							{
								if(lineSubjPart.endsWith("s")
								   && listOfArrayContains(nouns, newWord = lineSubjPart.substring(0, lineSubjPart.length()-1)))
								{
									lineSubj = lineSubj.replace(lineSubjPart, newWord);
									lineSubjGroupSfx = "s";
									lineSubjGroup = PLURAL;
								}

								else
								{
									for(int sp = 0;sp < nounSuffixes[PLURAL].size();sp++)
									{
										String pSuffix = nounSuffixes[PLURAL].get(sp);

										if((lineSubjPart.endsWith(pSuffix)
										   && lineSubjPart.length() >= pSuffix.length() && listOfArrayContains(nouns, newWord = lineSubjPart.substring(0, lineSubjPart.length() - pSuffix.length()) + nounSuffixes[SINGULAR].get(sp))))
										{
											if(lineSubjPart.length() > pSuffix.length())
											{
												lineSubj = lineSubj.replace(lineSubjPart, newWord);
												lineSubjGroupSfx = pSuffix;
											}
											else
												lineSubjGroupSfx = "";

											lineSubjGroup = PLURAL;
										}
									}
								}
							}

							if(lineSubjPart.equals("i"))
							{
								lineSubj = lineSubj.replace(lineSubj.startsWith("i")?"i " : " i", lineSubj.startsWith("i")?"you " : " you");
								if(lineVerb.equals("am"))
									lineVerb = "are";
							}
							else
							if(lineSubjPart.equals("you"))
							{
								lineSubj = lineSubj.replace(lineSubj.startsWith("you")?"you " : " you", lineSubj.startsWith("you")?"i " : " i");
								if(lineVerb.equals("are"))
									lineVerb = "am";
							}
							else
							if(lineSubjPart.equals("we"))
							{
								lineSubj = lineSubj.replace(lineSubj.startsWith("we")?"we " : " we", lineSubj.startsWith("we")?"you " : " you");
							}
							else
							if(lineSubjPart.equals("yours"))
							{
								lineSubj = "mine";
							}
							else
							if(lineSubjPart.equals("mine"))
							{
								lineSubj = "yours";
							}
							else
							if(lineSubjPart.equals("ours"))
							{
								lineSubj = "yours";
							}
						}
					}
					else
					{//subject with single word
						if(!listOfArrayContains(nouns, lineSubj))
						{
							String newWord;

							if(lineSubj.endsWith("s")
							   && listOfArrayContains(nouns, newWord = lineSubj.substring(0, lineSubj.length()-1)))
							{
								lineSubj = newWord;
								lineSubjGroupSfx = "s";
								lineSubjGroup = PLURAL;
							}

							else
							{
								for(int sp = 0;sp < nounSuffixes[PLURAL].size();sp++)
								{
									String pSuffix = nounSuffixes[PLURAL].get(sp);

									if((lineSubj.endsWith(pSuffix)
									   && listOfArrayContains(nouns, newWord = lineSubj.substring(0, lineSubj.length() - pSuffix.length()) + nounSuffixes[SINGULAR].get(sp))))
									{
										if(lineSubj.length() > pSuffix.length())
										{
											lineSubj = newWord;
											lineSubjGroupSfx = pSuffix;
										}
										else
											lineSubjGroupSfx = "";

										lineSubjGroup = PLURAL;
									}
								}
							}
						}

						if(lineSubj.equals("i"))
						{
							lineSubj = "you";
							if(lineVerb.equals("am"))
								lineVerb = "are";
						}
						else
						if(lineSubj.equals("you"))
						{
							lineSubj = "i";
							if(lineVerb.equals("are"))
								lineVerb = "am";
						}
						else
						if(lineSubj.equals("we"))
						{
							lineSubj = "you";
						}
						else
						if(lineSubj.equals("yours"))
						{
							lineSubj = "mine";
						}
						else
						if(lineSubj.equals("mine"))
						{
							lineSubj = "yours";
						}
						else
						if(lineSubj.equals("ours"))
						{
							lineSubj = "yours";
						}
					}

					//same but for object
					if(lineObj.contains(" "))
					{
						String[] lineObjParts = lineObj.split("[ ]+");
						String newWord;

						for(String lineObjPart : lineObjParts)
						{
							if(!listOfArrayContains(nouns, lineObjPart))
							{
								if(lineObjPart.endsWith("s")
								   && listOfArrayContains(nouns, newWord = lineObjPart.substring(0, lineObjPart.length()-1)))
								{
									lineObj = lineObj.replace(lineObjPart, newWord);
									lineObjGroupSfx = "s";
									lineObjGroup = PLURAL;
								}

								else
								{
									for(int sp = 0;sp < nounSuffixes[PLURAL].size();sp++)
									{
										String pSuffix = nounSuffixes[PLURAL].get(sp);

										if((lineObjPart.endsWith(pSuffix)
										   &&  lineObjPart.length() >= pSuffix.length()  && listOfArrayContains(nouns, newWord = lineObjPart.substring(0, lineObjPart.length() - pSuffix.length()) + nounSuffixes[SINGULAR].get(sp))))
										{
											if(lineObjPart.length() > pSuffix.length())
											{
												lineObj = lineObj.replace(lineObjPart, newWord);
												lineObjGroupSfx = pSuffix;
											}
											else
												lineObjGroupSfx = "";

											lineObjGroup = PLURAL;
										}
									}
								}
							}

							if(lineObjPart.equals("i"))
							{
								lineObj = lineObj.replace(lineObj.startsWith("i")?"i " : " i", lineObj.startsWith("i")?"you " : " you");
								if(lineVerb.equals("am"))
									lineVerb = "are";
							}
							else
							if(lineObjPart.equals("you"))
							{
								lineObj = lineObj.replace(lineObj.startsWith("you")?"you " : " you", lineObj.startsWith("you")?"i " : " i");
								if(lineVerb.equals("are"))
									lineVerb = "am";
							}
							else
							if(lineObjPart.equals("we"))
							{
								lineObj = lineObj.replace(lineObj.startsWith("we")?"we " : " we", lineObj.startsWith("we")?"you " : " you");
							}
							else
							if(lineObjPart.equals("yours"))
							{
								lineObj = "mine";
							}
							else
							if(lineObjPart.equals("mine"))
							{
								lineObj = "yours";
							}
							else
							if(lineObjPart.equals("ours"))
							{
								lineObj = "yours";
							}
						}
					}
					else
					{
						if(!listOfArrayContains(nouns, lineObj))
						{
							String newWord;

							if(lineObj.endsWith("s")
							   && listOfArrayContains(nouns, newWord = lineObj.substring(0, lineObj.length()-1)))
							{
								lineObj = newWord;
								lineObjGroupSfx = "s";
								lineObjGroup = PLURAL;
							}

							else
							{
								for(int sp = 0;sp < nounSuffixes[PLURAL].size();sp++)
								{
									String pSuffix = nounSuffixes[PLURAL].get(sp);

									if((lineObj.endsWith(pSuffix)
									   &&  lineObj.length() >= pSuffix.length()  && listOfArrayContains(nouns, newWord = lineObj.substring(0, lineObj.length() - pSuffix.length()) + nounSuffixes[SINGULAR].get(sp))))
									{
										if(lineObj.length() > pSuffix.length())
										{
											lineObjGroupSfx = pSuffix;
											lineObj = newWord;
										}
										else
											lineObjGroupSfx = "";

										lineObjGroup = PLURAL;
									}
								}
							}
						}

						if(lineObj.equals("i"))
						{
							lineObj = "you";
							if(lineVerb.equals("am"))
								lineVerb = "are";
						}
						else
						if(lineObj.equals("you"))
						{
							lineObj = "i";
							if(lineVerb.equals("are"))
								lineVerb = "am";
						}
						else
						if(lineObj.equals("we"))
						{
							lineObj = "you";
						}
						else
						if(lineObj.equals("yours"))
						{
							lineObj = "mine";
						}
						else
						if(lineObj.equals("mine"))
						{
							lineObj = "yours";
						}
						else
						if(lineObj.equals("ours"))
						{
							lineObj = "yours";
						}
					}

					for(String prepo : prepos)
					{
						if(lineAdj.equals(prepo))
							lineAdj = "";
						if(lineObjAdj.equals(prepo))
							lineObjAdj = "";
					}

					if(!lineAdj.trim().equals(""))
					{
						if(lineAdj.trim().equals("your"))
						{
							lineAdj = "my";
						}
						else
						if(lineAdj.trim().equals("my"))
						{
							lineAdj = "your";
						}
						else
						if(lineAdj.trim().equals("our"))
						{
							lineAdj = "your";
						}
					}

					if(!lineObjAdj.trim().equals(""))
					{
						if(lineObjAdj.trim().equals("your"))
						{
							lineObjAdj = "my";
						}
						else
						if(lineObjAdj.trim().equals("my"))
						{
							lineObjAdj = "your";
						}
						else
						if(lineObjAdj.trim().equals("our"))
						{
							lineObjAdj = "your";
						}
					}

					if(!lineSubj.equals("") && lineSubjIndex == -1)
					{
						if(!lineSubj.contains(" "))
						{
							for(int i = 0;i < words.size();i++)
								if(words.get(i).equals(lineSubj))
									lineSubjIndex = i;
						}
						else
						{
							String[] split = lineSubj.split("[ ]+");

							for(int i = 0;i < words.size();i++)
							{
								if(words.get(i).equals(split[0]))
								{
									for(int j = 0;j < split.length;j++)
									{
										if(!words.get(i + j).equals(split[j]))
											break;
										else
										if(j == split.length-1)
											lineSubjIndex = i + j;
									}
								}
							}
						}
					}

					if(!lineObj.equals("") && lineObjIndex == -1)
					{
						if(!lineObj.contains(" "))
						{
							for(int i = 0;i < words.size();i++)
								if(words.get(i).equals(lineObj))
									lineObjIndex = i;
						}
						else
						{
							String[] split = lineObj.split("[ ]+");

							for(int i = 0;i < words.size();i++)
							{
								if(words.get(i).equals(split[0]))
								{
									for(int j = 0;j < split.length;j++)
									{
										if(!words.get(i + j).equals(split[j]))
											break;
										else
										if(j == split.length-1)
											lineObjIndex = i + j;
									}
								}
							}
						}
					}

					if(!lineVerb.equals("") && lineVerbIndex == -1)
					{
						if(!lineVerb.contains(" "))
						{
							for(int i = 0;i < words.size();i++)
								if(words.get(i).equals(lineVerb))
									lineVerbIndex = i;
						}
						else
						{
							String[] split = lineVerb.split("[ ]+");

							for(int i = 0;i < words.size();i++)
							{
								if(words.get(i).equals(split[0]))
								{
									for(int j = 0;j < split.length;j++)
									{
										if(!words.get(i + j).equals(split[j]))
											break;
										else
										if(j == split.length-1)
											lineVerbIndex = i + j;
									}
								}
							}
						}
					}

					if(!lineAdj.equals("") && lineAdjIndex == -1)
					{
						if(!lineAdj.contains(" "))
						{
							for(int i = 0;i < words.size();i++)
								if(words.get(i).equals(lineAdj))
									lineAdjIndex = i;
						}
						else
						{
							String[] split = lineAdj.split("[ ]+");

							for(int i = 0;i < words.size();i++)
							{
								if(words.get(i).equals(split[0]))
								{
									for(int j = 0;j < split.length;j++)
									{
										if(!words.get(i + j).equals(split[j]))
											break;
										else
										if(j == split.length-1)
											lineAdjIndex = i + j;
									}
								}
							}
						}
					}

					if(!lineObjAdj.equals("") && lineObjAdjIndex == -1)
					{
						if(!lineObjAdj.contains(" "))
						{
							for(int i = 0;i < words.size();i++)
								if(words.get(i).equals(lineObjAdj))
									lineObjAdjIndex = i;
						}
						else
						{
							String[] split = lineObjAdj.split("[ ]+");

							for(int i = 0;i < words.size();i++)
							{
								if(words.get(i).equals(split[0]))
								{
									for(int j = 0;j < split.length;j++)
									{
										if(!words.get(i + j).equals(split[j]))
											break;
										else
										if(j == split.length-1)
											lineObjAdjIndex = i + j;
									}
								}
							}
						}
					}

					if(!lineAdv.equals("") && lineAdvIndex == -1)
					{
						if(!lineAdv.contains(" "))
						{
							for(int i = 0;i < words.size();i++)
								if(words.get(i).equals(lineAdv))
									lineAdvIndex = i;
						}
						else
						{
							String[] split = lineAdv.split("[ ]+");

							for(int i = 0;i < words.size();i++)
							{
								if(words.get(i).equals(split[0]))
								{
									for(int j = 0;j < split.length;j++)
									{
										if(!words.get(i + j).equals(split[j]))
											break;
										else
										if(j == split.length-1)
											lineAdvIndex = i + j;
									}
								}
							}
						}
					}

					if(lineSubj.contains(" "))
					{
						for(int q = 0;q < whWords.length;q++)
						{
							if(lineSubj.toLowerCase().contains((lineSubj.contains(" ")? (lineSubj.startsWith(whWords[q])? lineSubj + " " : lineSubj.endsWith(whWords[q])? " " + lineSubj : " " + lineSubj + " ") : lineSubj)))
							{
								lineQWord = whWords[q];
							}
						}
					}

					if(lineObj.contains(" "))
					{
						for(int q = 0;q < whWords.length;q++)
						{
							if(lineObj.toLowerCase().contains((lineObj.contains(" ")? (lineObj.startsWith(whWords[q])? lineObj + " " : lineObj.endsWith(whWords[q])? " " + lineObj : " " + lineObj + " ") : lineObj)))
							{
								lineQWord = whWords[q];
							}
						}
					}

					/*reply += "q : " + lineQWord + "\n";
					 reply += "adj : " + lineAdj + "\n";
					 reply += "subjGroup : " + (lineSubjGroup == SINGULAR? "singular" : "plural") + "\n";
					 reply += "subj : " + lineSubj + "\n";
					 reply += "subjSfx : " + lineSubjGroupSfx + "\n";
					 reply += "adv : " + lineAdv + "\n";
					 reply += "verb : " + lineVerb + "\n";
					 reply += "objAdj : " + lineObjAdj + "\n";
					 reply += "objGroup : " + (lineObjGroup == SINGULAR? "singular" : "plural") + "\n";
					 reply += "obj : " + lineObj + "\n";
					 reply += "objSfx : " + lineObjGroupSfx + "\n";
					 reply += "tense : " + lineTense + "\n";
					 reply += "type : " + lineType + "\n";
					 reply += "mod : " + lineMod + "\n";*/

					//find the type of sentence according to patterns in file

					if(!lineQWord.trim().equals(""))//either interrogative or exclamatory
					{
						if(!lineVerb.equals(""))
						{
							if(!lineObj.equals(""))
							{
								if(lineObjIndex > lineVerbIndex)
								{
									lineType = TYPE_INTERROGATIVE;
								}
								else
								{
									if(words.get(0).equals("how") || words.get(0).equals("what")
									   && (!lineAdj.equals("")|| !lineObjAdj.equals("") || !lineAdv.equals("")))
										lineType = TYPE_EXCLAMATORY;
									else
										lineType = TYPE_INTERROGATIVE;
								}
							}
							else
							{
								if(words.get(0).equals("how") || words.get(0).equals("what")
								   && (!lineAdj.equals("") || !lineObjAdj.equals("") || !lineAdv.equals("")))
									lineType = TYPE_EXCLAMATORY;
								else
									lineType = TYPE_INTERROGATIVE;
							}
						}
						else
						{
							if(words.get(0).equals("how") || words.get(0).equals("what")
							   && (!lineAdj.equals("") || !lineObjAdj.equals("") || !lineAdv.equals("")))
								lineType = TYPE_EXCLAMATORY;
							else
								lineType = TYPE_INTERROGATIVE;
						}
					}

					else

					if(!lineVerb.equals(""))//or assertive or imperative
					{
						if(!lineSubj.equals("") && lineVerbIndex > lineSubjIndex)
						{
							lineType = TYPE_ASSERTIVE;
						}
						else
						{
							lineType = TYPE_IMPERATIVE;
						}
					} 

					//if(lineType != TYPE_ABSTRACT)
					//{
					lineArgs = new String[]{lineQWord, lineAdj, String.valueOf(lineSubjGroup), lineSubj, lineAdv, lineVerb, lineObjAdj, String.valueOf(lineObjGroup), lineObj, String.valueOf(lineTense)};
					lineArgsInternal = lineQWord + argSplitStr + lineSubjDemon + argSplitStr + lineAdj + argSplitStr + String.valueOf(lineSubjGroup) + argSplitStr + lineSubj+lineSubjGroupSfx + argSplitStr + lineAdv + argSplitStr + lineVerb+lineVerbModSuffix + argSplitStr + lineObjDemon + argSplitStr  + lineObjAdj + argSplitStr + String.valueOf(lineObjGroup) + argSplitStr + lineObj+lineObjGroupSfx + argSplitStr + String.valueOf(lineTense) + argSplitStr + String.valueOf(lineType);// + argSplitStr + lineMod;
					//linesArgs += lineQWord + argSplitStr + lineAdj + argSplitStr + String.valueOf(lineSubjGroup) + argSplitStr + lineSubj + argSplitStr + lineAdv + argSplitStr + lineVerb + argSplitStr + lineObjAdj + argSplitStr + String.valueOf(lineObjGroup) + argSplitStr + lineObj + argSplitStr + String.valueOf(lineTense) + argSplitStr + String.valueOf(lineType) + "\n";

					for(int iPatns = sentencePatterns[REQ][lineType].size()-1;iPatns > -1;iPatns--)
					{
						boolean matches = false;

						for(int iPatn = sentencePatterns[REQ][lineType].get(iPatns).length-1;iPatn > -1;iPatn--)
						{
							String pattern = sentencePatterns[REQ][lineType].get(iPatns)[iPatn];
							String[] patternParts = pattern.split("[" + patnSplitStr + "]+");

							for(int i = 0;i < patternParts.length;i++)
							{
								String[] patternPartsParts;

								if(patternParts[i].contains("/"))
									patternPartsParts = patternParts[i].split("[/]+");
								else
									patternPartsParts = new String[]{patternParts[i]};

								matches = false;

								for(int j = 0;j < patternPartsParts.length;j++)
								{
									if(i < words.size())
									{
										//small vars or methods for checking index
										if(words.get(i).trim().equalsIgnoreCase(patternPartsParts[j].trim()))
										{
											matches = true;
											break;
										}
										else
										if(patternPartsParts[j].equals("word"))
										{
											if(!(lineAdvIndex == i
											   || lineVerbIndex == i
											   || lineAdjIndex == i
											   || lineSubjIndex == i
											   || lineObjAdjIndex == i
											   || lineObjIndex == i))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("subj"))
										{
											if(i == lineSubjIndex
											   || i == lineSubjIndex - (lineSubj.contains(" ")? lineSubj.split("[ ]+").length - 1 : 0))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("verb"))
										{
											if(i == lineVerbIndex || i == lineVerbIndex - (lineVerb.contains(" ")? lineVerb.split("[ ]+").length - 1 : 0))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("obj"))
										{
											if(i == lineObjIndex
											   || i == lineObjIndex - (lineObj.contains(" ")? lineObj.split("[ ]+").length - 1 : 0))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("adj"))
										{
											if(i == lineAdjIndex || i == lineAdjIndex - (lineAdj.contains(" ")? lineAdj.split("[ ]+").length - 1 : 0))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("objAdj"))
										{
											if(i == lineObjAdjIndex || i == lineObjAdjIndex - (lineObjAdj.contains(" ")? lineObjAdj.split("[ ]+").length - 1 : 0))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("adv"))
										{
											if(i == lineAdvIndex || i == lineAdvIndex - (lineAdv.contains(" ")? lineAdv.split("[ ]+").length - 1 : 0))
											{
												matches = true;
												break;
											}
										}
										else
										//capital words for checking if the word is a verb or noun or something
										if(patternPartsParts[j].equals("WORD"))
										{
											if(!(lineAdv.equals(patternPartsParts[j])
											   || lineVerb.equals(patternPartsParts[j])
											   || lineAdj.equals(patternPartsParts[j])
											   || lineSubj.equals(patternPartsParts[j])
											   || lineObjAdj.equals(patternPartsParts[j])
											   || lineObj.equals(patternPartsParts[j])))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("NOUN"))
										{
											if(listOfArrayContains(nouns, words.get(i)))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("VERB"))
										{
											if(listOfArrayContains(verbs, words.get(i)))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("ADJECTIVE"))
										{
											if(listOfArrayContains(adjs, words.get(i)))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("ADVERB"))
										{
											if(listOfArrayContains(advs, words.get(i)))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("!NOUN"))
										{
											if(!listOfArrayContains(nouns, words.get(i)))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("!VERB"))
										{
											if(!listOfArrayContains(verbs, words.get(i)))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("!ADJECTIVE"))
										{
											if(!listOfArrayContains(adjs, words.get(i)))
											{
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("!ADVERB"))
										{
											if(!listOfArrayContains(advs, words.get(i)))
											{
												matches = true;
												break;
											}
										}
									}

									if(!matches)
									{
										//function for actually replacing it by subject or verb or object or something
										if(patternPartsParts[j].equals("getSubj()"))
										{
											if(!lineSubj.equals(""))
											{
												patternPartsParts[j] = patternPartsParts[j].replace("getSubj()", lineSubj);
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("getVerb()"))
										{
											if(!lineVerb.equals(""))
											{
												patternPartsParts[j] = patternPartsParts[j].replace("getVerb()", lineVerb);
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("getObj()"))
										{
											if(!lineObj.equals(""))
											{
												patternPartsParts[j] = patternPartsParts[j].replace("getObj()", lineObj);
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("getAdj()"))
										{
											if(!lineAdj.equals(""))
											{
												patternPartsParts[j] = patternPartsParts[j].replace("getAdj()", lineAdj);
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("getObjAdj()"))
										{
											if(!lineObjAdj.equals(""))
											{
												patternPartsParts[j] = patternPartsParts[j].replace("getObjAdj()", lineObjAdj);
												matches = true;
												break;
											}
										}
										else
										if(patternPartsParts[j].equals("getAdv()"))
										{
											if(!lineAdv.equals(""))
											{
												patternPartsParts[j] = patternPartsParts[j].replace("getAdv()", lineAdv);
												matches = true;
												break;
											}
										}
										if(!matches)
											break;
									}
								}
								if(!matches)
									break;
							}

							if(matches)
							{
								int[] possibleTypes = new int[TYPE_LENGTH];
								int possibleTypesCount = 0;
								for(int type = 0;type < TYPE_LENGTH;type++)
									if(sentencePatterns[RES][type].get(iPatns).length > 0 && sentencePatterns[RES][type].get(iPatns) != new String[]{} && sentencePatterns[RES][type].get(iPatns)[0] != null && !sentencePatterns[RES][type].get(iPatns)[0].equals(""))
									{
										possibleTypes[possibleTypesCount] = type;
										possibleTypesCount++;
									}

								int replyType = possibleTypes[random.nextInt(possibleTypesCount)];//total types of sentences
								String[] possibleReplies = sentencePatterns[RES][replyType].get(iPatns);
								int replyIndex = random.nextInt(possibleReplies.length);
								String rPattern = sentencePatterns[RES][replyType].get(iPatns)[replyIndex];
								reply = rPattern.replaceAll("[" + patnSplitStr + "]+", patnSplitStr);

								//function for getting various variables

								if(reply.contains("getClause()"))
								{
									reply = reply.replace("getClause()", lineClause);
									reply = reply.replace("getClause()", "");
								}

								if(reply.contains("getSubj()"))
								{
									reply = reply.replace("getSubj()", (!reply.contains("getAdj()")? lineSubjDemon + " " : "") + lineSubj + lineSubjGroupSfx);
									reply = reply.replace("getSubj()", "");
								}

								if(reply.contains("getVerbModSuffix()"))
								{
									if(lineTense == TENSE_PRESENT)
									{
										if(lineSubjGroup == SINGULAR && !lineSubj.equals("i") && !lineSubj.equals("you"))
										{
											reply = reply.replace((lineVerb.equals(lineVerbModSuffix)? lineVerb : "") + "getVerbModSuffix()", lineVerbModSuffix);
										}
										else
											reply = reply.replace("getVerbModSuffix()", "");
									}
									else
									{
										reply = reply.replace((lineVerb.equals(lineVerbModSuffix)? lineVerb : "") + "getVerbModSuffix()", lineVerbModSuffix);
									}

									reply = reply.replace("getVerbModSuffix()", "");
								}

								if(reply.contains("getVerb()"))
								{
									reply = reply.replace("getVerb()", lineVerb);
									reply = reply.replace("getVerb()", "");
								}

								if(reply.contains("getObj()"))
								{
									reply = reply.replace("getObj()", (!reply.contains("getObjAdj()")? lineObjDemon + " " : "") + lineObj + lineObjGroupSfx);
									reply = reply.replace("getObj()", "");
								}

								if(reply.contains("getAdv()"))
								{
									reply = reply.replace("getAdv()", lineAdv);
									reply = reply.replace("getAdv()", "");
								}

								if(reply.contains("getAdj()"))
								{
									reply = reply.replace("getAdj()", lineSubjDemon + lineAdj);
									reply = reply.replace("getAdj()", "");
								}

								if(reply.contains("getObjAdj()"))
								{
									reply = reply.replace("getObjAdj()", lineObjDemon + lineObjAdj);
									reply = reply.replace("getObjAdj()", "");
								}

								if(reply.contains("getMod()"))
								{
									String mod = "";

									if(lineTense == TENSE_FUTURE)
									{
										mod = lineSubj.equals("i") || lineSubj.equals("we")  || lineSubj.equals("you")? "shall" : "will";
									}
									else
									if(lineTense == TENSE_PRESENT_PROGRESSIVE) 
									{
										mod =  lineSubjGroup == SINGULAR? !lineSubj.equals("i")? "is" : "am" : "are";
									}
									else
									if(lineTense == TENSE_PAST_PROGRESSIVE)
									{
										mod = lineSubjGroup == SINGULAR?  "was" : "were";
									}
									else
									if(lineTense == TENSE_FUTURE_PROGRESSIVE)
									{
										mod = lineSubj.equals("i") || lineSubj.equals("we") || lineSubj.equals("you")? "shall be" : "will be";
									}
									else
									if(lineTense == TENSE_PRESENT_PERFECT)
									{
										mod = (lineSubjGroup == SINGULAR? !lineSubj.equals("i")? "has" : "have" : "have");
									}
									else
									if(lineTense == TENSE_PRESENT_PERFECT_PROGRESSIVE)
									{
										mod = (lineSubjGroup == SINGULAR? !lineSubj.equals("i")? "has" : "have" : "have") + " " + "been";
									}
									else
									if(lineTense == TENSE_PAST_PERFECT)
									{
										mod = "had";
									}
									else
									if(lineTense == TENSE_PAST_PERFECT_PROGRESSIVE)
									{
										mod = "had been";
									}
									else
									if(lineTense == TENSE_FUTURE_PERFECT)
									{
										mod = (lineSubj.equals("i") || lineSubj.equals("we") || lineSubj.equals("you")? "shall" : "will") + "have";
									}
									else
									if(lineTense == TENSE_FUTURE_PERFECT_PROGRESSIVE)
									{
										mod = (lineSubj.equals("i") || lineSubj.equals("we") || lineSubj.equals("you")? "shall" : "will") + "have" + "been";
									}
									else
									{//simple present tense otherwise
										mod = mod = lineSubj.equals("i")? "am" : lineSubjGroup == SINGULAR?  "is" : "are";
									}
								}

								if(reply.contains("getData("))
								{
									for(int i = 0;i < 5;i++)
									{
										if(reply.contains("getData(" + String.valueOf(i) + ")"))
										{
											int[] misMatches = new int[userDatasArgs.size()];
											for(int m = 0;m < misMatches.length;m++)
												misMatches[m] = 0;

											for(int j = userDatasArgs.size()-1;j > -1;j--)
											{
												String[] args = userDatasArgs.get(j);
												boolean argMatches = false;

												for(int a = 0;a < args.length;a++)
												{
													if(args[a].equals(lineArgs[a]))
													{
														argMatches = true;
													}
													else
													if(args[a].equals("*") || args[a].trim().equals(""))
													{
														args[a] = lineArgs[a];
														argMatches = true;
													}
													else
													if(args[a].equals("+"))
													{
														if(!lineArgs[a].equals(""))
														{
															argMatches = true;
															args[a] = lineArgs[a];
														}
													}

													if(!argMatches)
														misMatches[j]++;
												}
											}

											ArrayList<Integer> lowestIndices = new ArrayList<Integer>();
											int lowestCount = Integer.MAX_VALUE;

											for(int m = misMatches.length-1;m > -1;m--)
											{
												if(!userDatas.get(m).equals(""))
												{
													if(misMatches[m] < lowestCount)
													{
														lowestCount = misMatches[m];
														lowestIndices = new ArrayList<Integer>();
														lowestIndices.add(m);
													}
													else
													if(misMatches[m] == lowestCount)
													{
														lowestCount = misMatches[m];
														lowestIndices.add(m);
													}
												}
											}

											if(lowestIndices.size() == 1)
												reply = reply.replace("getData(" + String.valueOf(i) + ")", userDatas.get(lowestIndices.get(0)));
											else
											{
												if(lowestIndices.size() > 1)
												{
													int[] intensity = new int[lowestIndices.size()];
													for(int in = 0;in < lowestIndices.size();in++)
													{
														for(int jn = 0;jn < lowestIndices.size();jn++)
														{
															if(userDatas.get(in).equals(userDatas.get(jn)))
															{
																intensity[in]++;
															}
														}
													}
													int highestInt = 0;
													int highestIntIndex = -1;
													for(int in = 0;in < intensity.length;in++)
													{
														if(intensity[in] > highestInt) 
														{
															highestInt = intensity[in];
															highestIntIndex = in;
														}
													}
													if(highestIntIndex != -1)
														reply = reply.replace("getData(" + String.valueOf(i) + ")", userDatas.get(highestIntIndex));
												}
											}
										}
									}
								}
								if(random.nextBoolean())
									break;
							}
						}
						if(matches)
						{
							if(random.nextBoolean())
								break;
						}
					}
					/*}
					 else
					 lineType = TYPE_ABSTRACT;*/

					/*if(lineType == TYPE_ASSERTIVE)
					 reply += "\nassertive\n";

					 if(lineType == TYPE_IMPERATIVE)
					 reply += "\nimperative\n";

					 if(lineType == TYPE_INTERROGATIVE)
					 reply += "\ninterrogative\n";

					 if(lineType == TYPE_EXCLAMATORY)
					 reply += "\nexclamatory\n";

					 for(String word : words)
					 reply += word + " ";

					 reply += "\n\n";*/ 

					rList.add(lineArgsInternal);
					rList.add(line);
					rList.add(lineClause);
				}
			}catch(Exception e)
			{
				return new String[]{""};//e.toString() + "\n\n" + e.getStackTrace()[0].toString()};
			}
		}

		rList.add(0, reply);

		return rList.toArray(new String[]{});
	}

	public String learn(String message, String reply)
	{
		String msg = message.trim().toLowerCase();
		String rly = reply.trim().toLowerCase();

		try
		{
			if(!msg.trim().equals("") && !rly.trim().equals(""))
			{
				boolean patnFound = false;
				boolean added = false;

				String[] msgData = getReply(msg);
				if(msgData.length > 1)
				{
					String[] msgLineArgs = msgData[1].split(argSplitStr);
					String msgLine = msgData[2];
					String msgLineClause = msgData[3];
					String msgLineQWord = msgLineArgs[0];
					String msgLineSubjDemon = msgLineArgs[1];
					String msgLineAdj = msgLineArgs[2];
					String msgLineSubjGroup = msgLineArgs[3];
					String msgLineSubj = msgLineArgs[4];
					String msgLineAdv = msgLineArgs[5];
					String msgLineVerb = msgLineArgs[6];
					String msgLineObjDemon = msgLineArgs[7];
					String msgLineObjAdj = msgLineArgs[8];
					String msgLineObjGroup = msgLineArgs[9];
					String msgLineObj = msgLineArgs[10];
					String msgLineTense = msgLineArgs[11];
					String msgLineTypeStr = msgLineArgs[12];
					int msgLineType = Integer.valueOf(msgLineTypeStr);

					//if(!msgLineVerb.equals(""))
					//{
					String[] rlyData = getReply(rly); 
					if(rlyData.length > 1) 
					{
						String[] rlyLineArgs = rlyData[1].split(argSplitStr);
						String rlyLine = rlyData[2];
						String rlyLineClause = rlyData[3];
						String rlyLineQWord = rlyLineArgs[0];
						String rlyLineSubjDemon = rlyLineArgs[1];
						String rlyLineAdj = rlyLineArgs[2];
						String rlyLineSubjGroup = rlyLineArgs[3];
						String rlyLineSubj = rlyLineArgs[4];
						String rlyLineAdv = rlyLineArgs[5];
						String rlyLineVerb = rlyLineArgs[6];
						String rlyLineObjDemon = rlyLineArgs[7];
						String rlyLineObjAdj = rlyLineArgs[8];
						String rlyLineObjGroup = rlyLineArgs[9];
						String rlyLineObj = rlyLineArgs[10];
						String rlyLineTense = rlyLineArgs[11];
						String rlyLineTypeStr = rlyLineArgs[12];
						String rlyLineMod = "";//rlyLineArgs[13];
						int rlyLineType = Integer.valueOf(rlyLineTypeStr);  

						String msgPatn = msgLine;
						String rlyPatn = rlyLine;

						String dataArgs = msgLineQWord + argSplitStr + msgLineAdj + argSplitStr + String.valueOf(msgLineSubjGroup) + argSplitStr + msgLineSubj + argSplitStr + msgLineAdv + argSplitStr + msgLineVerb + argSplitStr + msgLineObjAdj + argSplitStr + String.valueOf(msgLineObjGroup) + argSplitStr + msgLineObj + argSplitStr + String.valueOf(msgLineTense);
						String data = "";

						///to inverse the (change on "i" and "you" in Reply) made by getReply()
						if(rlyLineSubj.contains(" "))
						{//subject with multiple words
							String[] rlyLineSubjParts = rlyLineSubj.split("[ ]+");

							for(String rlyLineSubjPart : rlyLineSubjParts)
							{
								if(rlyLineSubjPart.equals("i"))
								{
									rlyLineSubj = rlyLineSubj.replace(rlyLineSubj.startsWith("i")?"i " : " i", rlyLineSubj.startsWith("i")?"you " : " you");
									if(rlyLineVerb.equals("am"))
										rlyLineVerb = "are";
								}
								else
								if(rlyLineSubjPart.equals("you"))
								{
									rlyLineSubj = rlyLineSubj.replace(rlyLineSubj.startsWith("you")?"you " : " you", rlyLineSubj.startsWith("you")?"i " : " i");
									if(rlyLineVerb.equals("are"))
										rlyLineVerb = "am";
								}
								else
								if(rlyLineSubjPart.equals("we"))
								{
									rlyLineSubj = rlyLineSubj.replace(rlyLineSubj.startsWith("we")?"we " : " we", rlyLineSubj.startsWith("we")?"you " : " you");
								}
								else
								if(rlyLineSubjPart.equals("yours"))
								{
									rlyLineSubj = "mine";
								}
								else
								if(rlyLineSubjPart.equals("mine"))
								{
									rlyLineSubj = "yours";
								}
								else
								if(rlyLineSubjPart.equals("ours"))
								{
									rlyLineSubj = "yours";
								}
							}
						}
						else
						{//subject with single word

							if(rlyLineSubj.equals("i"))
							{
								rlyLineSubj = "you";
								if(rlyLineVerb.equals("am"))
									rlyLineVerb = "are";
							}
							else
							if(rlyLineSubj.equals("you"))
							{
								rlyLineSubj = "i";
								if(rlyLineVerb.equals("are"))
									rlyLineVerb = "am";
							}
							else
							if(rlyLineSubj.equals("we"))
							{
								rlyLineSubj = "you";
							}
							else
							if(rlyLineSubj.equals("yours"))
							{
								rlyLineSubj = "mine";
							}
							else
							if(rlyLineSubj.equals("mine"))
							{
								rlyLineSubj = "yours";
							}
							else
							if(rlyLineSubj.equals("ours"))
							{
								rlyLineSubj = "yours";
							}
						}

						//same but for object
						if(rlyLineObj.contains(" "))
						{
							String[] rlyLineObjParts = rlyLineObj.split("[ ]+");

							for(String rlyLineObjPart : rlyLineObjParts)
							{
								if(rlyLineObjPart.equals("i"))
								{
									rlyLineObj = rlyLineObj.replace(rlyLineObj.startsWith("i")?"i " : " i", rlyLineObj.startsWith("i")?"you " : " you");
									if(rlyLineVerb.equals("am"))
										rlyLineVerb = "are";
								}
								else
								if(rlyLineObjPart.equals("you"))
								{
									rlyLineObj = rlyLineObj.replace(rlyLineObj.startsWith("you")?"you " : " you", rlyLineObj.startsWith("you")?"i " : " i");
									if(rlyLineVerb.equals("are"))
										rlyLineVerb = "am";
								}
								else
								if(rlyLineObjPart.equals("we"))
								{
									rlyLineObj = rlyLineObj.replace(rlyLineObj.startsWith("we")?"we " : " we", rlyLineObj.startsWith("we")?"you " : " you");
								}
								else
								if(rlyLineObjPart.equals("yours"))
								{
									rlyLineObj = "mine";
								}
								else
								if(rlyLineObjPart.equals("mine"))
								{
									rlyLineObj = "yours";
								}
								else
								if(rlyLineObjPart.equals("ours"))
								{
									rlyLineObj = "yours";
								}
							}
						}
						else
						{
							if(rlyLineObj.equals("i"))
							{
								rlyLineObj = "you";
								if(rlyLineVerb.equals("am"))
									rlyLineVerb = "are";
							}
							else
							if(rlyLineObj.equals("you"))
							{
								rlyLineObj = "i";
								if(rlyLineVerb.equals("are"))
									rlyLineVerb = "am";
							}
							else
							if(rlyLineObj.equals("we"))
							{
								rlyLineObj = "you";
							}
							else
							if(rlyLineObj.equals("yours"))
							{
								rlyLineObj = "mine";
							}
							else
							if(rlyLineObj.equals("mine"))
							{
								rlyLineObj = "yours";
							}
							else
							if(rlyLineObj.equals("ours"))
							{
								rlyLineObj = "yours";
							}
						}

						if(!rlyLineAdj.trim().equals(""))
						{
							if(rlyLineAdj.trim().equals("your"))
							{
								rlyLineAdj = "my";
							}
							else
							if(rlyLineAdj.trim().equals("my"))
							{
								rlyLineAdj = "your";
							}
							else
							if(rlyLineAdj.trim().equals("our"))
							{
								rlyLineAdj = "your";
							}
						}

						if(!rlyLineObjAdj.trim().equals(""))
						{
							if(rlyLineObjAdj.trim().equals("your"))
							{
								rlyLineObjAdj = "my";
							}
							else
							if(rlyLineObjAdj.trim().equals("my"))
							{
								rlyLineObjAdj = "your";
							}
							else
							if(rlyLineObjAdj.trim().equals("our"))
							{
								rlyLineObjAdj = "your";
							}
						}

						if(msgLineSubj.toLowerCase().contains((msgLineSubj.contains(" ")? (msgLineSubj.startsWith(msgLineQWord)? msgLineQWord + " " : msgLineSubj.endsWith(msgLineQWord)? " " + msgLineQWord : " " + msgLineQWord + " ") : msgLineQWord)))
						{
							if(msgLineObj.trim().equals(rlyLineSubj.trim()))
							{
								if(!rlyLineSubj.trim().equals(""))
									rlyPatn = rlyPatn.replace(rlyLine.startsWith(rlyLineSubj)? rlyLineSubj + " " : rlyLine.endsWith(rlyLineSubj)? " " + rlyLineSubj : " " + rlyLineSubj + " ", rlyLine.startsWith(rlyLineSubj)? "getObj()" + " " : rlyLine.endsWith(rlyLineSubj)? " " + "getObj()" : " " + "getObj()" + " ");
								if(!rlyLineAdj.trim().equals("") && !msgLineObjAdj.equals("") && rlyLineAdj.trim().equals(msgLineObjAdj.trim()))
									rlyPatn = rlyPatn.replace(rlyLine.startsWith(rlyLineAdj)? rlyLineAdj + " " : rlyLine.endsWith(rlyLineAdj)? " " + rlyLineAdj : " " + rlyLineAdj + " ", rlyLine.startsWith(rlyLineAdj)? "getObjAdj()" + " " : rlyLine.endsWith(rlyLineAdj)? " " + "getObjAdj()" : " " + "getObjAdj()" + " ");
								if(!rlyLineObjAdj.trim().equals(""))
									data = rlyLineObjAdj;
							}
						}
						else
						if(msgLineObj.toLowerCase().contains((msgLineObj.contains(" ")? (msgLineObj.startsWith(msgLineQWord)? msgLineQWord + " " : msgLineObj.endsWith(msgLineQWord)? " " + msgLineQWord : " " + msgLineQWord + " ") : msgLineQWord)))
						{
							if(msgLineSubj.trim().equals(rlyLineObj.trim()))
							{
								if(!rlyLineObj.trim().equals(""))
									rlyPatn = rlyPatn.replace(rlyLine.startsWith(rlyLineObj)? rlyLineObj + " " : rlyLine.endsWith(rlyLineObj)? " " + rlyLineObj : " " + rlyLineObj + " ", rlyLine.startsWith(rlyLineObj)? "getSubj()" + " " : rlyLine.endsWith(rlyLineObj)? " " + "getSubj()" : " " + "getSubj()" + " ");
								if(!rlyLineObjAdj.trim().equals("") && !msgLineAdj.equals("") && rlyLineObjAdj.trim().equals(msgLineAdj.trim()))
									rlyPatn = rlyPatn.replace(rlyLine.startsWith(rlyLineObjAdj)? rlyLineObjAdj + " " : rlyLine.endsWith(rlyLineObjAdj)? " " + rlyLineObjAdj : " " + rlyLineObjAdj + " ", rlyLine.startsWith(rlyLineObjAdj)? "getAdj()" + " " : rlyLine.endsWith(rlyLineObjAdj)? " " + "getAdj()" : " " + "getAdj()" + " ");
								if(!rlyLineAdj.trim().equals(""))
									data = rlyLineAdj;
							}
						}

						for(int q = 0;q < userDatasFilesName.length;q++)
						{
							if(msgLineQWord.equals(userDatasFilesName[q])
							   || msgLineSubj.toLowerCase().contains((msgLineSubj.contains(" ")? (msgLineSubj.startsWith(userDatasFilesName[q])? msgLineSubj + " " : msgLineSubj.endsWith(userDatasFilesName[q])? " " + msgLineSubj : " " + msgLineSubj + " ") : msgLineSubj))
							   || msgLineObj.toLowerCase().contains(userDatasFilesName[q]))
							{
								if(q < whWords.length)
								{
									///structure for below(copy-paste)
									/*if(msgLineSubj.toLowerCase().contains((msgLineSubj.contains(" ")? (msgLineSubj.startsWith(userDatasFilesName[q])? msgLineSubj + " " : msgLineSubj.endsWith(userDatasFilesName[q])? " " + msgLineSubj : " " + msgLineSubj + " ") : msgLineSubj))
									 {
									 if(msgLineObj.trim().equals(rlyLineObj.trim()))
									 {

									 }

									 else

									 if(msgLineObj.trim().equals(rlyLineSubj.trim()))
									 {
									 if(!rlyLineObj.trim().equals(""))

									 }
									 }
									 else
									 if(msgLineObj.toLowerCase().contains((msgLineObj.contains(" ")? (msgLineObj.startsWith(userDatasFilesName[q])? userDatasFilesName[q] + " " : msgLineObj.endsWith(userDatasFilesName[q])? " " + userDatasFilesName[q] : " " + userDatasFilesName[q] + " ") : userDatasFilesName[q]))))
									 {
									 if(msgLineSubj.trim().equals(rlyLineObj.trim()))
									 {
									 if(!rlyLineSubj.trim().equals(""))

									 }

									 else

									 if(msgLineSubj.trim().equals(rlyLineSubj.trim()))
									 {
									 if(!rlyLineObj.trim().equals(""))

									 }
									 }*/

									if(whWords[q].equals("what") || whWords[q].equals("who") || whWords[q].equals("whom") || whWords[q].equals("where"))
									{
										if(msgLineSubj.toLowerCase().contains((msgLineSubj.contains(" ")? (msgLineSubj.startsWith(userDatasFilesName[q])? userDatasFilesName[q] + " " : msgLineSubj.endsWith(userDatasFilesName[q])? " " + userDatasFilesName[q] : " " + userDatasFilesName[q] + " ") : userDatasFilesName[q])))
										{
											if(msgLineObj.trim().equals(rlyLineObj.trim()))
											{
												if(!rlyLineSubj.trim().equals(""))
												{
													String[] words = rlyLine.split("[ ]+");
													int adjIn = -1;
													int subjIn = -1;

													for(int i = 0;i < words.length;i++)
													{
														if(!rlyLineAdj.trim().contains(" "))
														{
															if(words[i].trim().equals(rlyLineAdj.trim()))
															{
																adjIn = i;
															}
														}
														else
														{
															String[] split = rlyLineAdj.split("[ ]+");

															for(int j = 0;j < split.length;j++)
															{
																if(!split[j].trim().equals(words[i + j].trim()))
																	break;
																else
																if(j == split.length-1)
																{
																	if(subjIn < j)
																		adjIn = j;
																	else
																		adjIn = i;
																}
															}
														}

														if(!rlyLineSubj.trim().contains(" "))
														{
															if(words[i].trim().equals(rlyLineSubj.trim()))
															{
																subjIn = i;
															}
														}
														else
														{
															String[] split = rlyLineSubj.split("[ ]+");

															for(int j = 0;j < split.length;j++)
															{
																if(!split[j].trim().equals(words[i + j].trim()))
																	break;
																else
																if(j == split.length-1)
																{
																	if(adjIn < j)
																		subjIn = j;
																	else
																		subjIn = i;
																}
															}
														}
													}

													if(adjIn != -1)
													{
														if(subjIn != -1)
														{
															if(adjIn < subjIn)
															{
																for(int i = adjIn;i <= subjIn;i++)
																	data += words[i] + " ";
															}
															else
															{
																if(adjIn > subjIn)
																{
																	for(int i = subjIn;i <= adjIn;i++)
																		data += words[i] + " ";
																}else
																	data = rlyLineSubj;
															}
														}
														else
															data = rlyLineSubj;
													}
													else
													{
														if(subjIn != -1)
														{
															data = rlyLineSubj;
														}
														else
															data = rlyLineSubj;
													}

													data = data.trim();

													if(rlyLine.contains(rlyLineSubjDemon + " " + data))
														data = rlyLineSubjDemon + " " + data;

													rlyLineAdj = "";
													rlyPatn = rlyPatn.replace(rlyLineObj, "getObj()");
												}
											}

											else

											if(msgLineObj.trim().equals(rlyLineSubj.trim()))
											{
												if(!rlyLineObj.trim().equals(""))
												{
													String[] words = rlyLine.split("[ ]+");
													int objAdjIn = -1;
													int objIn = -1;

													for(int i = 0;i < words.length;i++)
													{
														if(!rlyLineObjAdj.trim().contains(" "))
														{
															if(words[i].trim().equals(rlyLineObjAdj.trim()))
															{
																objAdjIn = i;
															}
														}
														else
														{
															String[] split = rlyLineObjAdj.split("[ ]+");

															for(int j = 0;j < split.length;j++)
															{
																if(!split[j].trim().equals(words[i + j].trim()))
																	break;
																else
																if(j == split.length-1)
																{
																	if(objIn < j)
																		objAdjIn = j;
																	else
																		objAdjIn = i;
																}
															}
														}

														if(!rlyLineObj.trim().contains(" "))
														{
															if(words[i].trim().equals(rlyLineObj.trim()))
															{
																objIn = i;
															}
														}
														else
														{
															String[] split = rlyLineObj.split("[ ]+");

															for(int j = 0;j < split.length;j++)
															{
																if(!split[j].trim().equals(words[i + j].trim()))
																	break;
																else
																if(j == split.length-1)
																{
																	if(objAdjIn < j)
																		objIn = j;
																	else
																		objIn = i;
																}
															}
														}
													}

													if(objAdjIn != -1)
													{
														if(objIn != -1)
														{
															if(objAdjIn < objIn)
															{
																for(int i = objAdjIn;i <= objIn;i++)
																	data += words[i] + " ";
															}
															else
															{
																if(objAdjIn > objIn)
																{
																	for(int i = objIn;i <= objAdjIn;i++)
																		data += words[i] + " ";
																}else
																	data = rlyLineObj;
															}
														}
														else
															data = rlyLineObj;
													}
													else
													{
														if(objIn != -1)
														{
															data = rlyLineObj;
														}
														else
															data = rlyLineObj;
													}

													data = data.trim();

													if(rlyLine.contains(rlyLineObjDemon + " " + data))
														data = rlyLineObjDemon + " " + data;

													rlyLineObjAdj = "";
													rlyPatn = rlyPatn.replace(rlyLineSubj, "getObj()");
												}
											}
										}

										else

										if(msgLineObj.toLowerCase().contains((msgLineObj.contains(" ")? (msgLineObj.startsWith(userDatasFilesName[q])? userDatasFilesName[q] + " " : msgLineObj.endsWith(userDatasFilesName[q])? " " + userDatasFilesName[q] : " " + userDatasFilesName[q] + " ") : userDatasFilesName[q])))
										{
											if(msgLineSubj.trim().equals(rlyLineSubj.trim()))
											{
												if(!rlyLineObj.trim().equals(""))
												{
													String[] words = rlyLine.split("[ ]+");
													int objAdjIn = -1;
													int objIn = -1;

													for(int i = 0;i < words.length;i++)
													{
														if(!rlyLineObjAdj.trim().contains(" "))
														{
															if(words[i].trim().equals(rlyLineObjAdj.trim()))
															{
																objAdjIn = i;
															}
														}
														else
														{
															String[] split = rlyLineObjAdj.split("[ ]+");

															for(int j = 0;j < split.length;j++)
															{
																if(!split[j].trim().equals(words[i + j].trim()))
																	break;
																else
																if(j == split.length-1)
																{
																	if(objIn < j)
																		objAdjIn = j;
																	else
																		objAdjIn = i;
																}
															}
														}

														if(!rlyLineObj.trim().contains(" "))
														{
															if(words[i].trim().equals(rlyLineObj.trim()))
															{
																objIn = i;
															}
														}
														else
														{
															String[] split = rlyLineObj.split("[ ]+");

															for(int j = 0;j < split.length;j++)
															{
																if(!split[j].trim().equals(words[i + j].trim()))
																	break;
																else
																if(j == split.length-1)
																{
																	if(objAdjIn < j)
																		objIn = j;
																	else
																		objIn = i;
																}
															}
														}
													}

													if(objAdjIn != -1)
													{
														if(objIn != -1)
														{
															if(objAdjIn < objIn)
															{
																for(int i = objAdjIn;i <= objIn;i++)
																	data += words[i] + " ";
															}
															else
															{
																if(objAdjIn > objIn)
																{
																	for(int i = objIn;i <= objAdjIn;i++)
																		data += words[i] + " ";
																}else
																	data = rlyLineObj;
															}
														}
														else
															data = rlyLineObj;
													}
													else
													{
														if(objIn != -1)
														{
															data = rlyLineObj;
														}
														else
															data = rlyLineObj;
													}

													data = data.trim();

													if(rlyLine.contains(rlyLineObjDemon + " " + data))
														data = rlyLineObjDemon + " " + data;

													rlyLineObjAdj = "";
													rlyPatn = rlyPatn.replace(rlyLineSubj, "getSubj()");
												}
											}

											else

											if(msgLineSubj.trim().equals(rlyLineObj.trim()))
											{
												if(!rlyLineSubj.trim().equals(""))
												{
													String[] words = rlyLine.split("[ ]+");
													int adjIn = -1;
													int subjIn = -1;

													for(int i = 0;i < words.length;i++)
													{
														if(!rlyLineAdj.trim().contains(" "))
														{
															if(words[i].trim().equals(rlyLineAdj.trim()))
															{
																adjIn = i;
															}
														}
														else
														{
															String[] split = rlyLineAdj.split("[ ]+");

															for(int j = 0;j < split.length;j++)
															{
																if(!split[j].trim().equals(words[i + j].trim()))
																	break;
																else
																if(j == split.length-1)
																{
																	if(subjIn < j)
																		adjIn = j;
																	else
																		adjIn = i;
																}
															}
														}

														if(!rlyLineSubj.trim().contains(" "))
														{
															if(words[i].trim().equals(rlyLineSubj.trim()))
															{
																subjIn = i;
															}
														}
														else
														{
															String[] split = rlyLineSubj.split("[ ]+");

															for(int j = 0;j < split.length;j++)
															{
																if(!split[j].trim().equals(words[i + j].trim()))
																	break;
																else
																if(j == split.length-1)
																{
																	if(adjIn < j)
																		subjIn = j;
																	else
																		subjIn = i;
																}
															}
														}
													}

													if(adjIn != -1)
													{
														if(subjIn != -1)
														{
															if(adjIn < subjIn)
															{
																for(int i = adjIn;i <= subjIn;i++)
																	data += words[i] + " ";
															}
															else
															{
																if(adjIn > subjIn)
																{
																	for(int i = subjIn;i <= adjIn;i++)
																		data += words[i] + " ";
																}else
																	data = rlyLineSubj;
															}
														}
														else
															data = rlyLineSubj;
													}
													else
													{
														if(subjIn != -1)
														{
															data = rlyLineSubj;
														}
														else
															data = rlyLineSubj;
													}

													data = data.trim();

													if(rlyLine.contains(rlyLineSubjDemon + " " + data))
														data = rlyLineSubjDemon + " " + data;

													rlyLineAdj = "";
													rlyPatn = rlyPatn.replace(rlyLineObj, "getSubj()");
												}
											}
										}
									}

									else

									if(whWords[q].equals("how"))
									{
										if(!data.trim().equals(""))
											if(msgLineQWord.equals(userDatasFilesName[q])
											   || msgLineSubj.toLowerCase().contains((msgLineSubj.contains(" ")? (msgLineSubj.startsWith(userDatasFilesName[q])? userDatasFilesName[q] + " " : msgLineSubj.endsWith(userDatasFilesName[q])? " " + userDatasFilesName[q] : " " + userDatasFilesName[q] + " ") : userDatasFilesName[q]))
											   || msgLineObj.toLowerCase().contains((msgLineObj.contains(" ")? (msgLineObj.startsWith(userDatasFilesName[q])? userDatasFilesName[q] + " " : msgLineObj.endsWith(userDatasFilesName[q])? " " + userDatasFilesName[q] : " " + userDatasFilesName[q] + " ") : userDatasFilesName[q])))
											{
												if(!rlyLineAdj.equals("") && !msgLineAdj.equals(rlyLineAdj) && !msgLineObjAdj.equals(rlyLineAdj))
													data = rlyLineAdj;
												else
												if(!rlyLineObjAdj.equals("") && !msgLineObjAdj.equals(rlyLineObjAdj) && !msgLineAdj.equals(rlyLineObjAdj))
													data = rlyLineObjAdj;
												else
												if(!rlyLineAdv.equals("") && !msgLineAdv.equals(rlyLineAdv))
													data = rlyLineAdv;
											}
									}
								}

								if(!data.trim().equals(""))
								{
									writeToFile(userDatasFile.get(q), new String[]{dataArgs + splitStr + data}, "SEPARATOR_NEW_LINE");
									userDatasArgs.add(new String[]{msgLineQWord, msgLineAdj, String.valueOf(msgLineSubjGroup), msgLineSubj, msgLineAdv, msgLineVerb, msgLineObjAdj, String.valueOf(msgLineObjGroup), msgLineObj, String.valueOf(msgLineTense)});
									userDatas.add(data);

									rlyPatn = rlyPatn.replace(rlyPatn.startsWith(data)? data + " " : rlyPatn.endsWith(data)? " " + data : " " + data + " ", rlyPatn.startsWith(data)? "getData(0)" + " " : rlyPatn.endsWith(data)? " " + "getData(0)" : " " + "getData(0)" + " "); 
									break;
								}
							}
						}

						if(data.trim().equals(""))
						{
							data = rlyLine;

							if(!msgLineAdj.equals(""))
								data = data.replace(data.startsWith(msgLineAdj)? msgLineAdj + " " : data.endsWith(msgLineAdj)? " " + msgLineAdj : " " + msgLineAdj + " ", data.startsWith(msgLineAdj)? "" + " " : data.endsWith(msgLineAdj)? " " + "" : " " + "" + " ");
							if(!msgLineSubj.trim().equals(""))
								data = data.replace(data.startsWith(msgLineSubj)? msgLineSubj + " " : data.endsWith(msgLineSubj)? " " + msgLineSubj : " " + msgLineSubj + " ", data.startsWith(msgLineSubj)? "" + " " : data.endsWith(msgLineSubj)? " " + "" : " " + "" + " ");
							if(!msgLineAdv.equals(""))
								data = data.replace(data.startsWith(msgLineAdv)? msgLineAdv + " " : data.endsWith(msgLineAdv)? " " + msgLineAdv : " " + msgLineAdv + " ", data.startsWith(msgLineAdv)? "" + " " : data.endsWith(msgLineAdv)? " " + "" : " " + "" + " ");
							if(!msgLineVerb.equals(""))
								data = data.replace(data.startsWith(msgLineVerb)? msgLineVerb + " " : data.endsWith(msgLineVerb)? " " + msgLineVerb : " " + msgLineVerb + " ", data.startsWith(msgLineVerb)? "" + " " : data.endsWith(msgLineVerb)? " " + "" : " " + "" + " ");
							if(!msgLineObjAdj.equals(""))
								data = data.replace(data.startsWith(msgLineObjAdj)? msgLineObjAdj + " " : data.endsWith(msgLineObjAdj)? " " + msgLineObjAdj : " " + msgLineObjAdj + " ", data.startsWith(msgLineObjAdj)? "" + " " : data.endsWith(msgLineObjAdj)? " " + "" : " " + "" + " ");
							if(!msgLineObj.trim().equals(""))
								data = data.replace(data.startsWith(msgLineObj)? msgLineObj + " " : data.endsWith(msgLineObj)? " " + msgLineObj : " " + msgLineObj + " ", data.startsWith(msgLineObj)? "" + " " : data.endsWith(msgLineObj)? " " + "" : " " + "" + " ");

							data = data.trim();

							boolean isDemon = false;
							for(String demon : demons)
								if(data.equals(demon))
									isDemon = true;

							if(!data.equals("") && !isDemon)
							{
								if(!rlyPatn.contains(data))
								{
									if(data.contains(" "))
									{
										String[] dataSplit = data.split("[ ]+");
										data = "";
										for(String s : dataSplit)
										{
											boolean isPartDemon = false;
											for(String demon : demons)
												if(s.equals(demon))
													isPartDemon = true;

											if(!s.trim().equals("") && !isPartDemon)
											{
												if(rlyPatn.contains((data + " " + s).trim()))
													data += " " + s;
												data = data.trim();
											}
										}
									}
								}

								if(rlyPatn.contains(data))
								{
									writeToFile(userDatasFile.get(userDatasFile.size()-1), new String[]{dataArgs + splitStr + data}, "SEPARATOR_NEW_LINE");
									userDatasArgs.add(new String[]{msgLineQWord, msgLineAdj, String.valueOf(msgLineSubjGroup), msgLineSubj, msgLineAdv, msgLineVerb, msgLineObjAdj, String.valueOf(msgLineObjGroup), msgLineObj, String.valueOf(msgLineTense)});
									userDatas.add(data);

									rlyPatn = rlyPatn.replace(rlyPatn.startsWith(data)? data + " " : rlyPatn.endsWith(data)? " " + data : " " + data + " ", rlyPatn.startsWith(data)? "getData(0)" + " " : rlyPatn.endsWith(data)? " " + "getData(0)" : " " + "getData(0)" + " ");
								}
							}
						}

						/*if(!msgLineClause.equals(""))
						 msgPatn = msgPatn.replace(msgPatn.startsWith(msgLineClause)? msgLineClause + " " : msgPatn.endsWith(msgLineClause)? " " + msgLineClause : " " + msgLineClause + " ", msgPatn.startsWith(msgLineClause)? "getCl()" + " " : msgPatn.endsWith(msgLineClause)? " " + "getCl()" : " " + "getCl()" + " ");*/
						if(!msgLineAdj.equals(""))
							msgPatn = msgPatn.replace(msgPatn.startsWith(msgLineAdj)? msgLineAdj + " " : msgPatn.endsWith(msgLineAdj)? " " + msgLineAdj : " " + msgLineAdj + " ", msgPatn.startsWith(msgLineAdj)? "adj" + " " : msgPatn.endsWith(msgLineAdj)? " " + "adj" : " " + "adj" + " ");
						if(!msgLineSubjDemon.trim().equals(""))
							msgPatn = msgPatn.replace(msgPatn.startsWith(msgLineSubjDemon)? msgLineSubjDemon + " " : msgPatn.endsWith(msgLineSubjDemon)? " " + msgLineSubjDemon : " " + msgLineSubjDemon + " ", msgPatn.startsWith(msgLineSubjDemon)? "" + " " : msgPatn.endsWith(msgLineSubjDemon)? " " + "" : " " + "" + " ");
						if(!msgLineSubj.trim().equals(""))
							msgPatn = msgPatn.replace(msgPatn.startsWith(msgLineSubj)? msgLineSubj + " " : msgPatn.endsWith(msgLineSubj)? " " + msgLineSubj : " " + msgLineSubj + " ", msgPatn.startsWith(msgLineSubj)? "subj" + " " : msgPatn.endsWith(msgLineSubj)? " " + "subj" : " " + "subj" + " ");
						if(!msgLineAdv.equals(""))
							msgPatn = msgPatn.replace(msgPatn.startsWith(msgLineAdv)? msgLineAdv + " " : msgPatn.endsWith(msgLineAdv)? " " + msgLineAdv : " " + msgLineAdv + " ", msgPatn.startsWith(msgLineAdv)? "adv" + " " : msgPatn.endsWith(msgLineAdv)? " " + "adv" : " " + "adv" + " ");
						if(!msgLineVerb.equals(""))
							msgPatn = msgPatn.replace(msgPatn.startsWith(msgLineVerb)? msgLineVerb + " " : msgPatn.endsWith(msgLineVerb)? " " + msgLineVerb : " " + msgLineVerb + " ", msgPatn.startsWith(msgLineVerb)? "verb" + " " : msgPatn.endsWith(msgLineVerb)? " " + "verb" : " " + "verb" + " ");
						if(!msgLineObjAdj.equals(""))
							msgPatn = msgPatn.replace(msgPatn.startsWith(msgLineObjAdj)? msgLineObjAdj + " " : msgPatn.endsWith(msgLineObjAdj)? " " + msgLineObjAdj : " " + msgLineObjAdj + " ", msgPatn.startsWith(msgLineObjAdj)? "objAdj" + " " : msgPatn.endsWith(msgLineObjAdj)? " " + "objAdj" : " " + "objAdj" + " ");
						if(!msgLineObjDemon.trim().equals(""))
							msgPatn = msgPatn.replace(msgPatn.startsWith(msgLineObjDemon)? msgLineObjDemon + " " : msgPatn.endsWith(msgLineObjDemon)? " " + msgLineObjDemon : " " + msgLineObjDemon + " ", msgPatn.startsWith(msgLineObjDemon)? "" + " " : msgPatn.endsWith(msgLineObjDemon)? " " + "" : " " + "" + " ");
						if(!msgLineObj.trim().equals(""))
							msgPatn = msgPatn.replace(msgPatn.startsWith(msgLineObj)? msgLineObj + " " : msgPatn.endsWith(msgLineObj)? " " + msgLineObj : " " + msgLineObj + " ", msgPatn.startsWith(msgLineObj)? "obj" + " " : msgPatn.endsWith(msgLineObj)? " " + "obj" : " " + "obj" + " ");

						if(!msgLineSubjDemon.trim().equals("") && !msgLineSubj.trim().equals(""))
						{
							if(msgPatn.contains(msgLineSubjDemon + " " + "subj"))
								msgPatn = msgPatn.replace(msgLineSubjDemon + " " + "subj", "subj");

							if(!msgLineObjAdj.trim().equals(""))
							{
								if(msgPatn.contains(msgLineSubjDemon + " " + "adj" + " " + "subj"))
									msgPatn = msgPatn.replace(msgLineSubjDemon + " " + "adj" + " " + "subj", "subj");
								if(msgPatn.contains("adj" + " " + msgLineSubjDemon + " " +  "subj"))
									msgPatn = msgPatn.replace("adj" + " " + msgLineSubjDemon + " " +  "subj", "subj");
							}

						}

						if(!msgLineObjDemon.trim().equals("") && !msgLineObj.trim().equals(""))
						{
							if(msgPatn.contains(msgLineObjDemon + " " + "obj"))
								msgPatn = msgPatn.replace(msgLineObjDemon + " " + "obj", "obj");

							if(!msgLineObjAdj.trim().equals("")) 
							{
								if(msgPatn.contains(msgLineObjDemon + " " + "objAdj" + " " + "obj"))
									msgPatn = msgPatn.replace(msgLineObjDemon + " " + "objAdj" + " " + "obj", "obj");
								if(msgPatn.contains("objAdj" + " " + msgLineSubjDemon + " " + "obj"))
									msgPatn = msgPatn.replace("objAdj" + " " + msgLineSubjDemon + " " +  "obj", "obj");
							}
						}

						String[] msgPatnParts = msgPatn.split("[ ]+");

						for(String msgPatnPart : msgPatnParts)
						{
							if(!msgPatnPart.equals(""))
							{
								boolean isMod = false;

								for(String tbv : simpleTenseVerbs)
									if(msgPatnPart.equals(tbv))
										isMod = true;

								for(String tbv2 : perfectTenseVerbs)
									if(msgPatnPart.equals(tbv2))
										isMod = true;

								for(String tbv3 : tenseVerbMods)
									if(msgPatnPart.equals(tbv3))
										isMod = true;

								for(String verbTenseMod : verbModVerbs)
									if(msgPatnPart.equals(verbTenseMod))
										isMod = true;

								if(isMod)
								{
									msgPatn = msgPatn.replace(msgPatn.startsWith(msgPatnPart)? msgPatnPart + " " : msgPatn.endsWith(msgPatnPart)? " " + msgPatnPart : " " + msgPatnPart + " ", msgPatn.startsWith(msgPatnPart)? "word" + " " : msgPatn.endsWith(msgPatnPart)? " " + "word" : " " + "word" + " ");
								}
							}
						}

						/*if(!rlyLineClause.equals(""))
						 rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineClause)? rlyLineClause + " " : rlyPatn.endsWith(rlyLineClause)? " " + rlyLineClause : " " + rlyLineClause + " ", rlyPatn.startsWith(rlyLineClause)? "getCl()" + " " : rlyPatn.endsWith(rlyLineClause)? " " + "getCl()" : " " + "getCl()" + " ");*/
						if(!rlyLineAdj.equals(""))
							rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineAdj)? rlyLineAdj + " " : rlyPatn.endsWith(rlyLineAdj)? " " + rlyLineAdj : " " + rlyLineAdj + " ", rlyPatn.startsWith(rlyLineAdj)? "getAdj()" + " " : rlyPatn.endsWith(rlyLineAdj)? " " + "getAdj()" : " " + "getAdj()" + " ");
						if(!rlyLineSubjDemon.trim().equals(""))
							rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineSubjDemon)? rlyLineSubjDemon + " " : rlyPatn.endsWith(rlyLineSubjDemon)? " " + rlyLineSubjDemon : " " + rlyLineSubjDemon + " ", rlyPatn.startsWith(rlyLineSubjDemon)? "" + " " : rlyPatn.endsWith(rlyLineSubjDemon)? " " + "" : " " + "" + " ");
						if(!rlyLineSubj.trim().equals(""))
							rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineSubj)? rlyLineSubj + " " : rlyPatn.endsWith(rlyLineSubj)? " " + rlyLineSubj : " " + rlyLineSubj + " ", rlyPatn.startsWith(rlyLineSubj)? "getSubj()" + " " : rlyPatn.endsWith(rlyLineSubj)? " " + "getSubj()" : " " + "getSubj()" + " ");
						if(!rlyLineAdv.equals(""))
							rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineAdv)? rlyLineAdv + " " : rlyPatn.endsWith(rlyLineAdv)? " " + rlyLineAdv : " " + rlyLineAdv + " ", rlyPatn.startsWith(rlyLineAdv)? "getAdv()" + " " : rlyPatn.endsWith(rlyLineAdv)? " " + "getAdv()" : " " + "getAdv()" + " ");
						if(!rlyLineObjAdj.equals(""))
							rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineObjAdj)? rlyLineObjAdj + " " : rlyPatn.endsWith(rlyLineObjAdj)? " " + rlyLineObjAdj : " " + rlyLineObjAdj + " ", rlyPatn.startsWith(rlyLineObjAdj)? "getObjAdj()" + " " : rlyPatn.endsWith(rlyLineObjAdj)? " " + "getObjAdj()" : " " + "getObjAdj()" + " ");
						if(!rlyLineObjDemon.trim().equals(""))
							rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineObjDemon)? rlyLineObjDemon + " " : rlyPatn.endsWith(rlyLineObjDemon)? " " + rlyLineObjDemon : " " + rlyLineObjDemon + " ", rlyPatn.startsWith(rlyLineObjDemon)? "" + " " : rlyPatn.endsWith(rlyLineObjDemon)? " " + "" : " " + "" + " ");
						if(!rlyLineObj.trim().equals(""))
							rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineObj)? rlyLineObj + " " : rlyPatn.endsWith(rlyLineObj)? " " + rlyLineObj : " " + rlyLineObj + " ", rlyPatn.startsWith(rlyLineObj)? "getObj()" + " " : rlyPatn.endsWith(rlyLineObj)? " " + "getObj()" : " " + "getObj()" + " ");
						if(!rlyLineMod.trim().equals(""))
							rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineMod)? rlyLineMod + " " : rlyPatn.endsWith(rlyLineMod)? " " + rlyLineMod : " " + rlyLineMod + " ", rlyPatn.startsWith(rlyLineMod)? "getMod()" + " " : rlyPatn.endsWith(rlyLineMod)? " " + "getMod()" : " " + "getMod()" + " ");

						if(!rlyLineSubjDemon.trim().equals("") && !rlyLineSubj.trim().equals(""))
						{
							if(rlyPatn.contains(rlyLineSubjDemon + " " + "getSubj()"))
								rlyPatn = rlyPatn.replace(rlyLineSubjDemon + " " + "getSubj()", "getSubj()");

							if(!rlyLineAdj.trim().equals(""))
							{
								if(rlyPatn.contains(rlyLineSubjDemon + " " + "getAdj()" + " " + "getSubj()"))
									rlyPatn = rlyPatn.replace(rlyLineSubjDemon + " " + "getAdj()" + " " + "getSubj()", "getSubj()");
								if(rlyPatn.contains("getAdj()" + " " + rlyLineSubjDemon + " " +  "getSubj()"))
									rlyPatn = rlyPatn.replace("getAdj()" + " " + rlyLineSubjDemon + " " +  "getSubj()", "getSubj()");
							}
						}

						if(!rlyLineObjDemon.trim().equals("") && !rlyLineObj.trim().equals(""))
						{
							if(rlyPatn.contains(rlyLineObjDemon + " " + "getObj()"))
								rlyPatn = rlyPatn.replace(rlyLineObjDemon + " " + "getObj()", "getObj()");

							if(!rlyLineObjAdj.trim().equals(""))
							{
								if(rlyPatn.contains(rlyLineObjDemon + " " + "getObjAdj()" + " " + "getObj()"))
									rlyPatn = rlyPatn.replace(rlyLineObjDemon + " " + "getObjAdj()" + " " + "getObj()", "getObj()");
								if(rlyPatn.contains("getObjAdj()" + " " + rlyLineSubjDemon + " " + "getObj()"))
									rlyPatn = rlyPatn.replace("getObjAdj()" + " " + rlyLineSubjDemon + " " +  "getObj()", "getObj()");
							}
						}

						if(!rlyLineVerb.equals(""))
						{
							//finding simple verb
							if(listOfArrayContains(verbs, rlyLineVerb))
							{
								rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineVerb)? rlyLineVerb + " " : rlyPatn.endsWith(rlyLineVerb)? " " + rlyLineVerb : " " + rlyLineVerb + " ", rlyPatn.startsWith(rlyLineVerb)? "getVerb()" + " " : rlyPatn.endsWith(rlyLineVerb)? " " + "getVerb()" : " " + "getVerb()" + " ");
							}
							else
							{
								//finding complex verbs and simple tense
								String suffix = "";
								String newWord;

								if(rlyLineVerb.endsWith("s")
								   && listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length()-1)))
								{
									suffix = "s";
								}
								else
								{
									if(rlyLineVerb.endsWith("es"))
									{
										if(listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length()-2)))
										{
											suffix = "es";
										}
										else
										{
											if(rlyLineVerb.length() >= 4 && rlyLineVerb.charAt(rlyLineVerb.length()-3) == rlyLineVerb.charAt(rlyLineVerb.length()-4))
											{
												boolean dub = true;

												for(char nD : noDubs)
													if(nD == rlyLineVerb.charAt(rlyLineVerb.length()-3))
														dub = false;

												//consonant-vowel-consonant rule
												if((dub && listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length()-3)))
												   || (!dub && listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length()-2))))
												{
													suffix = rlyLineVerb.replace(newWord, "");
												}
											}
										}
									}
									else
									{
										if(rlyLineVerb.endsWith("ed") || rlyLineVerb.endsWith("en"))
										{
											if(listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length()-2)))
											{
												suffix = rlyLineVerb.endsWith("ed")? "ed" : "en";
											}
											else
											{
												//consonant-vowel-consonant rule
												if(rlyLineVerb.length() >= 4 && rlyLineVerb.charAt(rlyLineVerb.length()-3) == rlyLineVerb.charAt(rlyLineVerb.length()-4))
												{
													boolean dub = true;

													for(char nD : noDubs)
														if(nD == rlyLineVerb.charAt(rlyLineVerb.length()-3))
															dub = false;
													if((dub && listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length()-3)))
													   || (!dub && listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length()-2))))
													{
														suffix = rlyLineVerb.replace(newWord, "");
													}
												}
											}
										}
										else
										if(rlyLineVerb.endsWith("ing"))
										{
											if(listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length()-3)))
											{
												suffix = "ing";
											}
											else
											{
												//consonant-vowel-consonant rule
												if(rlyLineVerb.length() >= 5 && rlyLineVerb.charAt(rlyLineVerb.length()-4) == rlyLineVerb.charAt(rlyLineVerb.length()-5))
												{
													boolean dub = true;

													for(char nD : noDubs)
														if(nD == rlyLineVerb.charAt(rlyLineVerb.length()-4))
															dub = false;

													if((dub && listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length()-4)))
													   || (!dub && listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length()-3))))
													{
														suffix = rlyLineVerb.replace(newWord, "");
													}
												}
											}
										}
									}
								}
								//some irregular suffixes to check
								for(int tense = 0;tense < verbSuffixes[PLURAL].length;tense++) 
								{
									ArrayList<String> pSuffixes = verbSuffixes[PLURAL][tense];

									for(int sp = 0;sp < pSuffixes.size();sp++)
									{
										String pSuffix = pSuffixes.get(sp);

										if((rlyLineVerb.endsWith(pSuffix)
										   && rlyLineVerb.length() >= pSuffix.length() && listOfArrayContains(verbs, newWord = rlyLineVerb.substring(0, rlyLineVerb.length() - pSuffix.length()) + verbSuffixes[SINGULAR][tense].get(sp)))
										   || (rlyLineVerb.equals(pSuffix)
										   && listOfArrayContains(verbs, newWord = verbSuffixes[SINGULAR][tense].get(sp))))
										{
											suffix = pSuffix;
										}
									}
								}
								rlyPatn = rlyPatn.replace(rlyPatn.startsWith(rlyLineVerb)? rlyLineVerb + " " : rlyPatn.endsWith(rlyLineVerb)? " " + rlyLineVerb : " " + rlyLineVerb + " ", rlyPatn.startsWith(rlyLineVerb)? "getVerb()getVerbModSuffix()" + " " : rlyPatn.endsWith(rlyLineVerb)? " " + "getVerb()getVerbModSuffix()" : " " + "getVerb()getVerbModSuffix()" + " ");
							}
						}

						for(int iPatns = 0;iPatns < sentencePatterns[REQ][msgLineType].size();iPatns++)
						{
							for(int iPatn = 0;iPatn < sentencePatterns[REQ][msgLineType].get(iPatns).length;iPatn++)
							{
								String pattern = sentencePatterns[REQ][msgLineType].get(iPatns)[iPatn];
								//File reqPatternFile = sentencePatternsFile[msgLineType].get(iPatns);

								if(pattern.equals(msgPatn))
								{
									patnFound = true;
									boolean otherPatnFound = false;

									for(int typee = 0;typee < TYPE_LENGTH;typee++)
									{
										for(int i = 0;i < sentencePatterns[RES][typee].get(iPatns).length;i++)
										{
											if(sentencePatterns[RES][typee].get(iPatns)[i].equals(rlyPatn))
											{
												added = false;
												otherPatnFound = true;
											}
										}
									}

									if(!otherPatnFound)
									{
										writeToFile(sentencePatternsFile[RES][rlyLineType].get(iPatns), new String[]{rlyPatn}, "SEPARATOR_NEW_LINE");
										String[] oldStrs = sentencePatterns[RES][rlyLineType].get(iPatns);
										String[] newStrs = new String[oldStrs.length+1];
										for(int s = 0;s < oldStrs.length;s++)
											newStrs[s] = oldStrs[s];
										newStrs[oldStrs.length] = rlyPatn;
										sentencePatterns[RES][rlyLineType].set(iPatns, newStrs);
										added = true;
									}

									break;
								}
							}
						}

						for(int iPatns = 0;iPatns < sentencePatterns[RES][msgLineType].size();iPatns++)
						{
							for(int iPatn = 0;iPatn < sentencePatterns[RES][msgLineType].get(iPatns).length;iPatn++)
							{
								String pattern = sentencePatterns[RES][msgLineType].get(iPatns)[iPatn];
								//File reqPatternFile = sentencePatternsFile[msgLineType].get(iPatns);

								if(pattern.equals(rlyPatn))
								{
									patnFound = true;
									boolean otherPatnFound = false;

									for(int typee = 0;typee < TYPE_LENGTH;typee++)
									{
										for(int i = 0;i < sentencePatterns[REQ][typee].get(iPatns).length;i++)
										{
											if(sentencePatterns[REQ][typee].get(iPatns)[i].equals(msgPatn))
											{
												added = false;
												otherPatnFound = true;
											}
										}
									}

									if(!otherPatnFound)
									{
										writeToFile(sentencePatternsFile[REQ][msgLineType].get(iPatns), new String[]{msgPatn}, "SEPARATOR_NEW_LINE");
										String[] oldStrs = sentencePatterns[REQ][msgLineType].get(iPatns);
										String[] newStrs = new String[oldStrs.length+1];
										for(int s = 0;s < oldStrs.length;s++)
											newStrs[s] = oldStrs[s];
										newStrs[oldStrs.length] = msgPatn;
										sentencePatterns[REQ][msgLineType].set(iPatns, newStrs);
										added = true;
									}

									break;
								}
							}
							//}

							/*if(!added)
							 {
							 if(patnFound)
							 { add to abstracts code of which is below below*/
						}
						//else
						//{
						if(!patnFound)
						{
							String msgFileFolder = typeStrs[msgLineType];
							String rlyFileFolder = typeStrs[rlyLineType];
							Random random = new Random();
							String name = String.valueOf(random.nextInt());

							while(new File(patnDir, "assertive" + patnDir.separator + name).exists())
								name = String.valueOf(random.nextInt()); 

							for(int t = 0;t < typeStrs.length;t++)
							{
								String typeStr = typeStrs[t];

								new File(patnDir, typeStr + patnDir.separator + name + ".req" + ".spatn").createNewFile();
								new File(patnDir, typeStr + patnDir.separator + name + ".res" + ".spatn").createNewFile();

								sentencePatterns[REQ][t].add(new String[]{});
								sentencePatterns[RES][t].add(new String[]{});
							}

							File msgFile = new File(patnDir, msgFileFolder + patnDir.separator + name + ".req" + ".spatn");
							File rlyFile = new File(patnDir, rlyFileFolder + patnDir.separator + name + ".res" + ".spatn");

							writeToFile(msgFile, new String[]{msgPatn}, "SEPARATOR_NEW_LINE");
							writeToFile(rlyFile, new String[]{rlyPatn}, "SEPARATOR_NEW_LINE");

							sentencePatterns[REQ][msgLineType].set(0, new String[]{msgPatn});
							sentencePatterns[RES][rlyLineType].set(0, new String[]{rlyPatn});
						}
					}
					//}
					//} 
				}
			}
		}
		catch(Exception e)
		{
			return "error learning reply pattern : " + e.getMessage();//e.toString() + "\n\n" + e.getStackTrace()[0];
		}
		finally
		{
			if(!msg.trim().equals("") && !rly.trim().equals(""))
			{
				try
				{
					File reqFile = null;
					File resFile = null;
					boolean filesFound = false;

					for(int f = 0;f < abstractsFileName[REQ].size();f++)
					{
						String fName = abstractsFileName[REQ].get(f);
						String[] strs = fName.contains(",")? fName.split("[,]+") : new String[]{fName};
						for(String str : strs)
						{
							if(str.replace(".req", "").equals(msg.trim().toLowerCase()))
							{
								reqFile = new File(absDir, fName);
								File resTmpFile = new File(absDir, fName.replace(".req", ".res"));
								if(!resTmpFile.exists())
									resTmpFile.createNewFile();
								String[] resData = readFromFile(resTmpFile, "SEPARATOR_NEW_LINE");
								boolean found = false;
								for(String res : resData)
									if(res.trim().toLowerCase().equals(rly.trim().toLowerCase()))
									{
										found = true;
										break;
									}
								if(!found)
								{
									resFile = resTmpFile;
									writeToFile(resFile, new String[]{rly}, "SEPARATOR_NEW_LINE");
									String[] resPrevData = abstracts[RES].get(f);
									String[] resNewData = new String[resPrevData.length+1];
									for(int rd = 0;rd < resPrevData.length;rd++)
										resNewData[rd] = resPrevData[rd];
									resNewData[resPrevData.length] = rly;
									abstracts[RES].set(f, resNewData);
								}
								else
									resFile = null;

								filesFound = true;
								break;
							}
						}
						if(filesFound)
							break;
					}

					if(resFile == null)
					{
						for(int f = 0;f < abstractsFileName[RES].size();f++)
						{
							String fName = abstractsFileName[RES].get(f);
							File resTmpFile = new File(absDir, fName);
							String[] resData = readFromFile(resTmpFile, "SEPARATOR_NEW_LINE");
							boolean found = false;
							for(String res : resData)
								if(res.trim().toLowerCase().equals(rly.trim().toLowerCase()))
								{
									found = true;
									break;
								}

							if(found)
							{
								found = false;

								String[] strs = fName.contains(",")? fName.split("[,]+") : new String[]{fName};
								for(String str : strs)
								{
									if(str.replace(".res", "").equals(msg.trim().toLowerCase()))
									{
										found = true;
										break;
									}
								}

								if(!found)
								{
									resFile = resTmpFile;
									reqFile = new File(absDir, fName.replace(".res", ".req"));
									if(!reqFile.exists())
										reqFile.createNewFile();


									File reqNewFile = new File(absDir, fName.replace(".res", "," + msg.trim().toLowerCase() + ".req"));
									File resNewFile = new File(absDir, fName.replace(".res", "," + msg.trim().toLowerCase() + ".res"));
									reqFile.renameTo(reqNewFile);
									resFile.renameTo(resNewFile);
									reqFile = reqNewFile;
									resFile = resNewFile;

									writeToFile(reqFile, new String[]{msg}, "SEPARATOR_NEW_LINE");

									abstractsFileName[REQ].set(f, reqFile.getName());
									abstractsFileName[RES].set(f, resFile.getName());

									String[] reqPrevData = abstracts[REQ].get(f);
									String[] reqNewData = new String[reqPrevData.length+1];
									for(int rd = 0;rd < reqPrevData.length;rd++)
										reqNewData[rd] = reqPrevData[rd];
									reqNewData[reqPrevData.length] = msg;
									abstracts[REQ].set(f, reqNewData);
								}
								else
									reqFile = null;

								filesFound = true;
								break;
							}
						}
					}

					if(!filesFound)
					{
						reqFile = new File(absDir, msg.trim().toLowerCase() + ".req");
						resFile = new File(absDir, msg.trim().toLowerCase() + ".res");

						reqFile.createNewFile();
						resFile.createNewFile();

						writeToFile(reqFile, new String[]{msg}, "SEPARATOR_NEW_LINE");
						writeToFile(resFile, new String[]{rly}, "SEPARATOR_NEW_LINE");

						abstractsFileName[REQ].add(reqFile.getName());
						abstractsFileName[RES].add(resFile.getName());

						abstracts[REQ].add(new String[]{msg});
						abstracts[RES].add(new String[]{rly});
					}
				}catch(Exception e)
				{
					return "error learning reply : " + e.getMessage();//e.toString() + "\n\n" + e.getStackTrace()[0];
				}
			}
		}
		return "learned";
	}

	public boolean listOfArrayContains(ArrayList<String[]> list, String word)
	{
		for(int i = 0;i < list.size();i++)
		{
			for(String item : list.get(i))
			{
				if(word.equals(item.toLowerCase()))
				{
					return true;
				}
			}
		}
		return false;
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
