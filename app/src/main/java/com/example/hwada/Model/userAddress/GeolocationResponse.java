// GeolocationResponse.java

//YApi QuickType插件生成，具体参考文档:https://github.com/RmondJone/YapiQuickType

package com.example.hwada.Model.userAddress;

import java.util.List;

public class GeolocationResponse {
    private List<Suggestion> suggestions;

    public List<Suggestion> getSuggestions() { return suggestions; }
    public void setSuggestions(List<Suggestion> value) { this.suggestions = value; }

    @Override
    public String toString() {
        return "GeolocationResponse{" +
                "suggestions=" + suggestions +
                '}';
    }
}

