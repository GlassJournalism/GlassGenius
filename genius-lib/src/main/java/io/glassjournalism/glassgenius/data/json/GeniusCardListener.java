package io.glassjournalism.glassgenius.data.json;

public interface GeniusCardListener {

    public void onKeywordsLoaded();
    public void onError(String error);
    public void onCardFound(CardFoundResponse cardFound);
}
