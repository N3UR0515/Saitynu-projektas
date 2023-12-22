// App.js

import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import RegistrationForm from './auth/RegistrationForm';
import Dashboard from './auth/Dashboard';
import React from 'react';
import LoginForm from './auth/LoginForm';
import CampaignList from './campaign/CampaignList';
import CreateCampaign from './campaign/CreateCampaign';
import UpdateCampaign from './campaign/UpdateCampaign';
import Characters from './character/Characters';
import Skills from './skills/Skills';
import Traits from './trait/Traits';
import UserComponent from './user/UserComponent';
import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';

library.add(fas);


const App = () => {
  return (
    <Router>
      <Routes>
      <Route path="/campaigns" element={<CampaignList/>} />
        <Route path="/users" element={<UserComponent/>} />
        <Route path="/register" element={<RegistrationForm/>} />
        <Route path="/dashboard" element={<Dashboard/>} />
        <Route path="/login" element={<LoginForm/>} />
        <Route path="/createcampaign" element={<CreateCampaign/>}/>
        <Route path="/updatecampaign/:campaignId" element={<UpdateCampaign/>}/>
        <Route path="/campaigns/:campaignId/characters" element={<Characters/>}/>
        <Route path="/campaigns/:campaignId/characters/:characterId/skills" element={<Skills/>}/>
        <Route path="/campaigns/:campaignId/characters/:characterId/traits" element={<Traits/>}/>
      </Routes>
    </Router>
  );
};

export default App;
