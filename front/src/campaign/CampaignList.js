import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { Link, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import Header1 from '../reuse/Header';
import Footer from '../reuse/Footer';

const CampaignContainer = styled.div`
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f4f4f4;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
`;

const CustomText = styled.div`
  font-family: 'Titan One', sans-serif;
`;

const CampaignHeader = styled.h1`
  color: #333;
  text-align: center;
`;

const CampaignItem = styled.li`
  list-style-type: none;
  margin: 10px 0;
  padding: 10px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const DeleteButton = styled.button`
  background-color: #ff6961;
  color: #fff;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  align-items: right;
`;

const NewCampaignButton = styled.button`
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #45a049;
  }
`;

const ModalContainer = styled.div`
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-width: 400px;
  width: 100%;
  z-index: 100;

  /* Apply transition to opacity and visibility */
  opacity: 0;
  visibility: hidden;
  transition: opacity 1s ease, visibility 1s ease;

  /* Apply styles for the active state */
  &.active {
    opacity: 1;
    visibility: visible;
    transition: opacity 1s ease, visibility 1s ease;
  }
`;

const EditButtonContainer = styled.div`
  display: flex;
  justify-content: right;
`;

const EditButton = styled.button`
  background-color: #2196f3;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 10px;

  &:hover {
    background-color: #0b7dda;
  }
`;

const CreateCampaignButton = styled.button`
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;

  &:hover {
    background-color: #45a049;
  }
`;

const Label = styled.label`
  margin-bottom: 10px;
`;

const Input = styled.input`
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
`;

const ErrorMessage = styled.div`
  color: #ff0000;
  margin-top: 10px;
`;

const List = styled.ul`
  list-style: none;
  padding: 0;
`;

// Utility function for making authenticated requests
const makeAuthenticatedRequest = async (url, method, data = null) => {
  const token = Cookies.get('token');
  const refreshToken = Cookies.get('refreshToken');

  try {
    return await axios({
      method,
      url,
      data,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  } catch (error) {
    if (error.response && error.response.status === 403) {
      // Token is invalid or expired, attempt refreshing
      try {
        const refreshResponse = await axios.get('http://localhost:8080/api/v1/auth/refresh', {
        headers: {
          Authorization: `Bearer ${refreshToken}`,
        }});

        const newAccessToken = refreshResponse.data.token;
        Cookies.set('token', newAccessToken);

        // Retry the original request with the new access token
        return await axios({
          method,
          url,
          data,
          headers: {
            Authorization: `Bearer ${newAccessToken}`,
          },
        });
      } catch (refreshError) {
        // Token refresh failed, redirect to login
        throw refreshError;
      }
    } else {
      // Other errors, rethrow the error
      throw error;
    }
  }
};

const CampaignList = () => {
  const [campaigns, setCampaigns] = useState([]);
  const [showCreateCampaignModal, setShowCreateCampaignModal] = useState(false);
  const [showEditCampaignModal, setShowEditCampaignModal] = useState(false);
  const [name, setCampaignName] = useState('');
  const [ptsLimit, setPtsLimit] = useState('');
  const [editCampaignId, setEditCampaignId] = useState(null);
  const [editError, setEditError] = useState(null);
  const [createError, setCreateError] = useState(null);
  const navigate = useNavigate();

  const fetchCampaigns = async () => {
    try {
      const response = await makeAuthenticatedRequest('http://localhost:8080/api/campaign/', 'get');
      setCampaigns(response.data);
    } catch (error) {
      console.error('Error fetching campaigns:', error);
      navigate('/login');
    }
  };

  useEffect(() => {
    fetchCampaigns();
  }, []); // Run this effect only once on component mount

  const handleDelete = async (campaignId) => {
    try {
      await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}`, 'delete');
      // Update the local state by removing the deleted campaign
      setCampaigns((prevCampaigns) => prevCampaigns.filter((campaign) => campaign.id !== campaignId));
      // Clear any previous error messages
      setEditError(null);
    } catch (error) {
      setEditError('An error occurred while deleting the campaign. Please try again later.');
    }
  };

  const openCreateCampaignModal = () => {
    setShowCreateCampaignModal(true);
  };

  const closeCreateCampaignModal = () => {
    setShowCreateCampaignModal(false);
  };

  const handleCreateCampaign = async (e) => {
    e.preventDefault();
    try {
      await makeAuthenticatedRequest('http://localhost:8080/api/campaign/', 'post', {
        name: name,
        ptsLimit: ptsLimit,
      });
      // Handle success, e.g., redirect or update the campaign list
    } catch (error) {
      if (error.response && error.response.status === 403) {
        setCreateError('You do not have sufficient privileges to create this campaign.');
      } else {
        setCreateError('An error occurred while creating the campaign. Please check your input and try again.');
      }
    }
  };

  const openEditCampaignModal = (campaignId, campaignName, campaignPtsLimit) => {
    setEditCampaignId(campaignId);
    setCampaignName(campaignName);
    setPtsLimit(campaignPtsLimit);
    setShowEditCampaignModal(true);
  };

  const closeEditCampaignModal = () => {
    setEditCampaignId(null);
    setShowEditCampaignModal(false);
  };

  const handleEditCampaign = async (e) => {
    e.preventDefault();
    try {
      await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${editCampaignId}`, 'put', {
        name: name,
        ptsLimit: ptsLimit,
      });
      // Handle success, e.g., redirect or update the campaign list
      closeEditCampaignModal();
      // Clear any previous error messages
      setEditError(null);
    } catch (error) {
      if (error.response && error.response.status === 403) {
        setEditError('You do not have sufficient privileges to edit this campaign.');
      } else {
        setEditError('An error occurred while updating the campaign. Please check your input and try again.');
      }
    }
  };

  return (
    <CampaignContainer>
      <Header1 />
      <CampaignHeader>
        <CustomText>Campaign List</CustomText>
        <EditButtonContainer>
          <NewCampaignButton onClick={openCreateCampaignModal}>
            New Campaign
          </NewCampaignButton>
        </EditButtonContainer>
      </CampaignHeader>
      <List>
        {editError && <ErrorMessage>{editError}</ErrorMessage>}
        {campaigns.map((campaign) => (
          <CampaignItem key={campaign.id}>
            <Link to={`/campaigns/${campaign.id}/characters`}>
              {campaign.name} - {campaign.ptsLimit}
            </Link>
            <EditButtonContainer>
              <EditButton onClick={() => openEditCampaignModal(campaign.id, campaign.name, campaign.ptsLimit)}>
                Edit
              </EditButton>
              <DeleteButton onClick={() => handleDelete(campaign.id)}>
                Delete
              </DeleteButton>
            </EditButtonContainer>
          </CampaignItem>
        ))}
      </List>

      {/* Render the CreateCampaign modal if showCreateCampaignModal is true */}
      {true && (
        <ModalContainer className={showCreateCampaignModal ? 'active' : ''}>
          <h1>Create Campaign</h1>
          <Label>Campaign Name:</Label>
          <Input type="text" value={name} onChange={(e) => setCampaignName(e.target.value)} />
          <br />
          <Label>Points Limit:</Label>
          <Input type="number" value={ptsLimit} onChange={(e) => setPtsLimit(e.target.value)} />
          <br />
          <CreateCampaignButton onClick={handleCreateCampaign}>Create Campaign</CreateCampaignButton>
          <NewCampaignButton onClick={closeCreateCampaignModal}>Close</NewCampaignButton>
          {createError && <ErrorMessage>{createError}</ErrorMessage>}
        </ModalContainer>
      )}

      {/* Render the EditCampaign modal if showEditCampaignModal is true */}
      {true && (
        <ModalContainer className={showEditCampaignModal ? 'active' : ''}>
          <h1>Edit Campaign</h1>
          <Label>Campaign Name:</Label>
          <Input type="text" value={name} onChange={(e) => setCampaignName(e.target.value)} />
          <br />
          <Label>Points Limit:</Label>
          <Input type="number" value={ptsLimit} onChange={(e) => setPtsLimit(e.target.value)} />
          <br />
          <CreateCampaignButton onClick={handleEditCampaign}>Save Changes</CreateCampaignButton>
          <NewCampaignButton onClick={closeEditCampaignModal}>Close</NewCampaignButton>
          {editError && <ErrorMessage>{editError}</ErrorMessage>}
        </ModalContainer>
      )}
      <Footer />
    </CampaignContainer>
  );
};

export default CampaignList;
