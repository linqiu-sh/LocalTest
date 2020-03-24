package com.stubhub.messaging.networkInvoke.brazeMetadata;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeListCampaignsResponse;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class BrazeMetadataUpdateTask implements Callable<BrazeMetadata> {

    private BrazeClient brazeClient;

    private BrazeMetadataUpdateTask(){}

    public BrazeMetadataUpdateTask(BrazeClient brazeClient) {
        this.brazeClient = brazeClient;
    }

    @Override
    public BrazeMetadata call() throws Exception {
        BrazeListCampaignsResponse brazeListCampaignsResponse = brazeClient.listAllCampaigns();
        if (brazeListCampaignsResponse == null){
            //TODO throw business exception
            return null;
        }
        List<CampaignMetadata> campaigns = brazeListCampaignsResponse.getCampaigns();
        if (campaigns == null || campaigns.isEmpty()){
            //TODO throw business exception
            return null;
        }

        Map<String, CampaignMetadata> name2CampaignMap = new HashMap<>();
        Map<String, CampaignMetadata> tag2CampaignMap  = new HashMap<>();
        for (CampaignMetadata cm : campaigns){
            String name = cm.getName();
            List<String> tags = cm.getTags();
            if (name != null && !name.isEmpty()){
                name2CampaignMap.put(name, cm);
            }
            if (tags != null && !tags.isEmpty()){
                for (String tag : tags){
                    if (tag == null || tag.isEmpty()){
                        continue;
                    }
                    tag2CampaignMap.put(tag, cm);
                }
            }
        }

        return new BrazeMetadata(name2CampaignMap, tag2CampaignMap);
    }
}
