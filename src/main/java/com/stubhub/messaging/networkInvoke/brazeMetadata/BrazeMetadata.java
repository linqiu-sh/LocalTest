package com.stubhub.messaging.networkInvoke.brazeMetadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class BrazeMetadata {

    private final Map<String, CampaignMetadata> name2CampaignMap;
    private final Map<String, List<CampaignMetadata>> tag2CampaignMap;

    public CampaignMetadata getCampaignMetadataByName(String name){
        if (name2CampaignMap == null || name2CampaignMap.isEmpty()){
            return null;
        }

        return name2CampaignMap.get(name);
    }

    public List<CampaignMetadata> getCampaignMetadataByTag(String tag){
        if (tag2CampaignMap == null || tag2CampaignMap.isEmpty()){
            return null;
        }

        return tag2CampaignMap.get(tag);
    }
}
