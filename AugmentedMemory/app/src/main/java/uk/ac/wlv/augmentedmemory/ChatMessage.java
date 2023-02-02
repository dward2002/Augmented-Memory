package uk.ac.wlv.augmentedmemory;

class ChatMessage {
    private String id;

    private String mTitle;
    private String name;
    private boolean read;

    public ChatMessage() {
    }

    public ChatMessage(String mTitle, String name) {
        this.mTitle = mTitle;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getmTitle() {return mTitle;}

    public void setmTitle(String mTitle) {this.mTitle = mTitle;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRead() {return read;}

    public void setRead(boolean read) {this.read = read;}

}