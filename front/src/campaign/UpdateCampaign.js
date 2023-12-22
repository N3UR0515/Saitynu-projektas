// UpdateCampaign.js
import React, { useState } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { useParams } from 'react-router-dom';

const UpdateCampaign = () => {
  const [name, setCampaignName] = useState('');
  const [ptsLimit, setPtsLimit] = useState('');
  const {campaignId, characterId} = useParams();


  const handleUpdateCampaign = () => {
    const token = Cookies.get('token');
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    // Make a PUT request to update the campaign
    axios.put(`http://localhost:8080/api/campaign/${campaignId}`, {
        name: name,
        ptsLimit: ptsLimit,
    })
      .then(response => {
        console.log('Campaign updated:', response.data);
        // Handle success, e.g., redirect or update the campaign list
      })
      .catch(error => {
        console.error('Error updating campaign:', error);
        // Handle error
      });
  };

  return (
    <div>
      <h1>Update Campaign</h1>
      <label>Campaign Name:</label>
      <input type="text" value={name} onChange={(e) => setCampaignName(e.target.value)} />
      <br />
      <label>Points Limit:</label>
      <input type="text" value={ptsLimit} onChange={(e) => setPtsLimit(e.target.value)} />
      <br />
      <button onClick={handleUpdateCampaign}>Update Campaign</button>
    </div>
  );
};

export default UpdateCampaign;
