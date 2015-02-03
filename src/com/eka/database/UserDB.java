//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.eka.database;

import com.eka.types.Locale;
import com.eka.types.WordState;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class UserDB {
    Map<String, Integer> data_en;
    Map<String, Integer> data_es;
    Map<String, Integer> data_ch;
    Map<String, Integer> data_cur;
    Map<String, Map<String, Integer>> data;

    public UserDB() {
    }

    public void open(String workingDir) {
        this.data = new HashMap<>();
        data_ch = new HashMap<>();
        data_en = new HashMap<>();
        data_es = new HashMap<>();
        data.put("en", data_en);
        data.put("es", data_es);
        data.put("ch", data_ch);


        try {
            FileInputStream e = new FileInputStream(workingDir + "data/data.dat");
            Throwable var3 = null;

            try {
                ObjectInputStream x2 = new ObjectInputStream(e);
                Throwable var5 = null;

                try {
                    this.data = (Map) x2.readObject();
                    this.data_en = (Map) this.data.get("en");
                    this.data_es = (Map) this.data.get("es");
                    this.data_ch = (Map) this.data.get("ch");
                } catch (Throwable var30) {
                    var5 = var30;
                    throw var30;
                } finally {
                    if (x2 != null) {
                        if (var5 != null) {
                            try {
                                x2.close();
                            } catch (Throwable var29) {
                                var5.addSuppressed(var29);
                            }
                        } else {
                            x2.close();
                        }
                    }

                }
            } catch (Throwable var32) {
                var3 = var32;
                throw var32;
            } finally {
                if (e != null) {
                    if (var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var28) {
                            var3.addSuppressed(var28);
                        }
                    } else {
                        e.close();
                    }
                }

            }
        } catch (ClassNotFoundException | IOException var34) {
            var34.printStackTrace();
        }

    }

    public void create(String workingDir) {
        try {
            FileOutputStream e = new FileOutputStream(workingDir + "data/data.dat");
            Throwable var3 = null;

            try {
                ObjectOutputStream x2 = new ObjectOutputStream(e);
                Throwable var5 = null;

                try {
                    x2.writeObject(this.data);
                } catch (Throwable var30) {
                    var5 = var30;
                    throw var30;
                } finally {
                    if (x2 != null) {
                        if (var5 != null) {
                            try {
                                x2.close();
                            } catch (Throwable var29) {
                                var5.addSuppressed(var29);
                            }
                        } else {
                            x2.close();
                        }
                    }

                }
            } catch (Throwable var32) {
                var3 = var32;
                throw var32;
            } finally {
                if (e != null) {
                    if (var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var28) {
                            var3.addSuppressed(var28);
                        }
                    } else {
                        e.close();
                    }
                }

            }
        } catch (IOException var34) {
            var34.printStackTrace();
        }

    }

    public void setLanguage(Locale locale) {
        if (this.data == null) {
            System.out.println("in setLanguage: no data loaded");
        } else {
            switch (locale) {
                case EN:
                    this.data_cur = this.data_en;
                    break;
                case ES:
                    this.data_cur = this.data_es;
                    break;
                case CH:
                    this.data_cur = this.data_ch;
            }

        }
    }

    public void save(String workingDir) {
        if (this.data != null) {
            try {
                FileOutputStream e = new FileOutputStream(workingDir + "data/data.dat");
                Throwable var3 = null;

                try {
                    ObjectOutputStream x2 = new ObjectOutputStream(e);
                    Throwable var5 = null;

                    try {
                        x2.writeObject(this.data);
                    } catch (Throwable var30) {
                        var5 = var30;
                        throw var30;
                    } finally {
                        if (x2 != null) {
                            if (var5 != null) {
                                try {
                                    x2.close();
                                } catch (Throwable var29) {
                                    var5.addSuppressed(var29);
                                }
                            } else {
                                x2.close();
                            }
                        }

                    }
                } catch (Throwable var32) {
                    var3 = var32;
                    throw var32;
                } finally {
                    if (e != null) {
                        if (var3 != null) {
                            try {
                                e.close();
                            } catch (Throwable var28) {
                                var3.addSuppressed(var28);
                            }
                        } else {
                            e.close();
                        }
                    }

                }
            } catch (IOException var34) {
                var34.printStackTrace();
            }

        }
    }

    public WordState getWordStatus(String word) {
        Integer status = (Integer) this.data_cur.get(word.toLowerCase());
        if (status == null)
            return WordState.UNKNOWN;
        else if (status == 1)
            return WordState.KNOWN;
        else if (status == 2)
            return WordState.NAME;
        else if (status == 3)
            return WordState.LEARNING;
        else
            throw new RuntimeException("Incorrect data");
    }

    public void setWordStatus(String word, WordState newStatus) {
        if (newStatus == WordState.KNOWN) {
            this.data_cur.put(word.toLowerCase(), 1);
        } else if (newStatus == WordState.NAME) {
            this.data_cur.put(word.toLowerCase(), 2);
        } else if (newStatus == WordState.LEARNING) {
            this.data_cur.put(word.toLowerCase(), 3);
        } else if (newStatus == WordState.UNKNOWN) {
            this.data_cur.remove(word.toLowerCase());
        }

    }

    public int getSize() {
        return this.data_cur.size();
    }


}
