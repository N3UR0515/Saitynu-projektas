import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { useParams, Link } from 'react-router-dom';
import styled from 'styled-components';
import Header1 from '../reuse/Header';
import Footer from '../reuse/Footer';

const TraitsContainer = styled.div`
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

const Table = styled.table`
  width: 100%;
  border-spacing: 10px;
`;

const TableRow = styled.tr`
  display: table-row;
`;

const TableCell = styled.td`
  display: table-cell;
`;

const Title = styled.h1`
  color: #333;
  text-align: center;
`;

const TraitList = styled.ul`
  list-style: none;
  padding: 0;
`;

const TraitItem = styled.li`
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  margin: 10px 0;
  padding: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const TraitInfo = styled.div`
  flex-grow: 1;
`;

const TraitActions = styled.div``;

const FormContainer = styled.div`
  margin-top: 20px;
`;

const FormTitle = styled.h2`
  color: #333;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
`;

const FormLabel = styled.label`
  margin-bottom: 10px;
  color: #555;
`;

const FormInput = styled.input`
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
`;

const FormButton = styled.button`
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

const DeleteButton = styled.button`
  background-color: #ff0000;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #cc0000;
  }
`;

const ErrorMessage = styled.div`
  color: #ff0000;
  margin-top: 10px;
`;

const makeAuthenticatedRequest = async (url, method, data = null) => {
  const token = Cookies.get('token');
  const refreshToken = Cookies.get('refreshToken');

  //axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
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

const Traits = () => {
    const {campaignId, characterId} = useParams();
  const [traits, setTraits] = useState([]);
  const [newTrait, setNewTrait] = useState({
    name: '',
    description: '',
    price: 0,
    // Add other trait properties here
  });
  const [selectedTrait, setSelectedTrait] = useState(null);
  const [error, setError] = useState(null);

  const fetchTraits = async () => {
    const token = Cookies.get('token');
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    try {
      const response = await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters/${characterId}/traits`, 'get');
      setTraits(response.data);
    } catch (error) {
      console.error('Error fetching traits:', error);
    }
  };

  useEffect(() => {
    // Fetch traits from the backend when the component mounts
    fetchTraits();
  }, []);

  const addTrait = async () => {
    const token = Cookies.get('token');
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    try {
      // Send a POST request to add a new trait
      const response = await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters/${characterId}/traits`, 'post', newTrait);
      // Optionally, update the local state with the new trait
      setTraits([...traits, response.data]);
      // Clear the form
      setNewTrait({
        name: '',
        description: '',
        price: 0,
        // Clear other trait properties here
      });
    } catch (error) {
      if (error.response && error.response.status === 422) {
        setError('Bad input.')
      }
      else
      {
        setError('Insufficient privileges.');
      }
    }
  };

  const updateTrait = async (traitId) => {
    const token = Cookies.get('token');
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    try {
      // Send a PUT request to update the selected trait
      const response = await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters/${characterId}/traits/${traitId}`, 'put', newTrait);
      // Update the local state with the updated trait
      setTraits((prevTraits) =>
        prevTraits.map((trait) => (trait.id === selectedTrait.id ? response.data : trait))
      );
      // Clear the form and reset selected trait
      setNewTrait({
        name: '',
        description: '',
        price: 0,
        // Clear other trait properties here
      });
      setSelectedTrait(null);
    } catch (error) {
      if (error.response && error.response.status === 422) {
        setError('Bad input.')
      }
      else
      {
        setError('Insufficient privileges.');
      }
    }
  };

  const selectTrait = (trait) => {
    const token = Cookies.get('token');
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    // Set the selected trait for editing
    setSelectedTrait(trait);
    // Populate the form with the selected trait's data
    setNewTrait({
      name: trait.name,
      description: trait.description || '',
      price: trait.price,
      // Populate other trait properties here
    });
  };

  const deleteTrait = async (traitId) => {
    const token = Cookies.get('token');
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    try {
      // Send a DELETE request to remove the selected trait
      await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters/${characterId}/traits/${traitId}`, 'delete');
      // Update the local state by removing the deleted trait
      setTraits((prevTraits) => prevTraits.filter((trait) => trait.id !== traitId));
      setNewTrait({
        name: '',
        description: '',
        price: 0,
      });
      setSelectedTrait(null);
      setError(null);
    } catch (error) {
      console.error('Error deleting trait:', error);
    }
  };

  const clearForm = () => {
    setSelectedTrait(null);
    setNewTrait({
      name: '',
      description: '',
      price: 0,
    });
    setError(null);
  };

  return (
    <TraitsContainer>
      <Header1 />
      <Title><CustomText>Traits</CustomText></Title>

      <Link to={`/campaigns/${campaignId}/characters`}>
            <FormButton>
              Back to Character List
            </FormButton>
          </Link>

      {/* Trait list */}
      <TraitList>
        {traits.map((trait) => (
          <TraitItem key={trait.id} onClick={() => selectTrait(trait)}>
            <TraitInfo>
            {trait.name}
            </TraitInfo>
            <TraitActions>
            {/* Display other trait properties here */}
            <DeleteButton onClick={(e) => deleteTrait(trait.id)}>Delete</DeleteButton>
            </TraitActions>
          </TraitItem>
        ))}
      </TraitList>

      {/* Add or Update trait form */}
      <FormContainer>
      <FormTitle>{selectedTrait ? 'Update Trait' : 'Add Trait'}</FormTitle>
      <Form>
        <Table>
          <TableRow>
            <TableCell>
        <FormLabel>
          Name:
          </FormLabel>
          </TableCell>
          <TableCell>
          <FormInput
            type="text"
            value={newTrait.name}
            onChange={(e) => setNewTrait({ ...newTrait, name: e.target.value })}
          />
          </TableCell>
          </TableRow>
        
        <TableRow>
          <TableCell>
        <FormLabel>
          Description:
          </FormLabel>
          </TableCell>
          <TableCell>
          <FormInput
            type="text"
            value={newTrait.description}
            onChange={(e) => setNewTrait({ ...newTrait, description: e.target.value })}
          />
          </TableCell>
          </TableRow>
        
        <TableRow>
          <TableCell>
        <FormLabel>
          Price:
          </FormLabel>
          </TableCell>
          <TableCell>
          <FormInput
            type="number"
            value={newTrait.price}
            onChange={(e) => setNewTrait({ ...newTrait, price: parseInt(e.target.value, 10) })}
          />
          </TableCell>
          </TableRow>
          </Table>
        
        <TableRow>
          <TableCell>
        <FormButton type="button" onClick={selectedTrait ? () => updateTrait(selectedTrait.id) : () => addTrait(newTrait.campaignId, newTrait.characterId)}>
          {selectedTrait ? 'Update Trait' : 'Add Trait'}
        </FormButton>
        </TableCell>
        <TableCell>

        {selectedTrait && (
            <FormButton type="button" onClick={() => clearForm()}>
            New Trait
          </FormButton>
          )}
          </TableCell>
          </TableRow>

        {error && <ErrorMessage>{error}</ErrorMessage>}
        
        </Form>
      </FormContainer>
      <Footer />
    </TraitsContainer>
  );
};

export default Traits;
