package com.stubhub.messaging.networkInvoke.brazeMetadata;

import com.stubhub.messaging.networkInvoke.brazeModel.BrazeListCampaignsResponse;
import com.stubhub.messaging.networkInvoke.exception.BrazeBusinessException;
import com.stubhub.messaging.networkInvoke.repository.BrazeClient;

import java.sql.Timestamp;
import java.util.ArrayList;
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
            throw new BrazeBusinessException("BrazeListCampaignsResponse is null!");
        }
        List<CampaignMetadata> campaigns = brazeListCampaignsResponse.getCampaigns();
        if (campaigns == null || campaigns.isEmpty()){
            //TODO throw business exception
            throw new BrazeBusinessException("No Campaign exists in Metadata Response!");
        }

        Map<String, CampaignMetadata> name2CampaignMap = new HashMap<>();
        Map<String, List<CampaignMetadata>> tag2CampaignsMap  = new HashMap<>();
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
                    List<CampaignMetadata> campaignMetadataList = tag2CampaignsMap.get(tag);
                    if (campaignMetadataList == null){
                        campaignMetadataList = new ArrayList<>();
                        tag2CampaignsMap.put(tag, campaignMetadataList);
                    }
                    campaignMetadataList.add(cm);
                }
            }
        }

        return new BrazeMetadata(name2CampaignMap, tag2CampaignsMap);
    }
}
