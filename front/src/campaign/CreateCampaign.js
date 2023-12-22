// CreateCampaign.js
import React, { useState } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';

const CreateCampaign = () => {
  const [name, setCampaignName] = useState('');
  const [ptsLimit, setPtsLimit] = useState('');

  const handleCreateCampaign = async (e) => {
    e.preventDefault();
    const token = Cookies.get('token');
    // Make a POST request to create a new campaign
    console.log(token);
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    await axios.post('http://localhost:8080/api/campaign/', {
      name: name,
      ptsLimit: ptsLimit,
    })
      .then(response => {
        console.log('Campaign created:', response.data);
        // Handle success, e.g., redirect or update the campaign list
      })
      .catch(error => {
        console.error('Error creating campaign:', error);
        console.error(e);
        // Handle error
      });
  };

  return (
    <div>
      <h1>Create Campaign</h1>
      <label>Campaign Name:</label>
      <input type="text" value={name} onChange={(e) => setCampaignName(e.target.value)} />
      <br />
      <label>Points Limit:</label>
      <input type="text" value={ptsLimit} onChange={(e) => setPtsLimit(e.target.value)} />
      <br />
      <button onClick={handleCreateCampaign}>Create Campaign</button>
    </div>
  );
};

export default CreateCampaign;
