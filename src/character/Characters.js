import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { Navigate, useParams } from 'react-router-dom';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import Header1 from '../reuse/Header';
import Footer1 from '../reuse/Footer';

// Styled components
const CharactersContainer = styled.div`
  max-width: 1000px;
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

const Label = styled.label`
  margin-bottom: 10px;
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

const CharacterList = styled.ul`
  list-style: none;
  padding: 0;
`;

const CharacterItem = styled.li`
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  margin: 10px 0;
  padding: 10px;
  transition: background-color 0.3s ease;

  &:hover {
    background-color: #f9f9f9;
  }
`;

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
  display: block;
  width: 150px; /* Set a consistent width for your labels */
  margin-bottom: 10px;
  color: #555;
`;

const FormInput = styled.input`
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box; /* Include padding and border in the element's total width and height */
  width: calc(100% - 16px); /* Adjust the width to fill the available space, considering padding */
`;

const FormButton = styled.button`
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  margin: 10px;

  &:hover {
    background-color: #45a049;
  }
`;

const ErrorMessage = styled.div`
  color: #ff0000;
  margin-top: 10px;
`;

const EditButtonContainer = styled.div`
  display: flex;
  justify-content: right;
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

const CharacterImage = styled.img`
  display: block;
  max-width: 100%;
  height: auto;
  margin: 0 auto;
`;

const ImageContainer = styled.div`
  max-width: 600px;
  margin: 0 auto;
`;

// Utility function for making authenticated requests
const makeAuthenticatedRequest = async (url, method, data = null) => {
  const token = Cookies.get('token');
  const refreshToken = Cookies.get('refreshToken');

  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  try {
    return await axios({
      method,
      url,
      data,
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

// Component
const Characters = () => {
  const [characters, setCharacters] = useState([]);
  const { campaignId } = useParams();
  const token = Cookies.get('token');
  const [newCharacter, setNewCharacter] = useState({
    photo: '',
    first_name: '',
    last_name: '',
    strength: 0,
    dex: 0,
    intelligence: 0,
    health: 0,
    hitPoints: 0,
    will: 0,
    perception: 0,
    basicSpeed: 0.0,
    basicMove: 0,
    hidden: false,
  });
  const [selectedCharacter, setSelectedCharacter] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchCharacters();
  }, []);

  const fetchCharacters = async () => {
    try {
      const response = await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters`, 'get');
      setCharacters(response.data);
    } catch (error) {
      console.error('Error fetching characters:', error);
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];

    // Check if a file is selected
    if (file) {
      // Use FileReader to read the contents of the file and convert it to a data URL
      const reader = new FileReader();
      reader.onloadend = () => {
        setNewCharacter((prevCharacter) => ({
          ...prevCharacter,
          photo: reader.result, // Set the data URL as the value for 'photo'
        }));
      };
      reader.readAsDataURL(file);
    }
  };


  const addCharacter = async () => {
    try {
      newCharacter.basicSpeed = parseFloat(newCharacter.basicSpeed);
      newCharacter.strength = parseInt(newCharacter.strength);
      newCharacter.dex = parseInt(newCharacter.dex);
      newCharacter.intelligence = parseInt(newCharacter.intelligence);
      newCharacter.health = parseInt(newCharacter.health);
      newCharacter.hitPoints = parseInt(newCharacter.hitPoints);
      newCharacter.will = parseInt(newCharacter.will);
      newCharacter.perception = parseInt(newCharacter.perception);
      newCharacter.basicMove = parseInt(newCharacter.basicMove);
      const response = await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters`, 'post', newCharacter);
      setCharacters([...characters, response.data]);
      setNewCharacter({
        photo: '',
        first_name: '',
        last_name: '',
        strength: 0,
        dex: 0,
        intelligence: 0,
        health: 0,
        hitPoints: 0,
        will: 0,
        perception: 0,
        basicSpeed: 0.0,
        basicMove: 0,
        hidden: false,
      });
      setError(null);
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

  const updateCharacter = async () => {
    try {
      newCharacter.basicSpeed = parseFloat(newCharacter.basicSpeed);
      newCharacter.strength = parseInt(newCharacter.strength);
      newCharacter.dex = parseInt(newCharacter.dex);
      newCharacter.intelligence = parseInt(newCharacter.intelligence);
      newCharacter.health = parseInt(newCharacter.health);
      newCharacter.hitPoints = parseInt(newCharacter.hitPoints);
      newCharacter.will = parseInt(newCharacter.will);
      newCharacter.perception = parseInt(newCharacter.perception);
      newCharacter.basicMove = parseInt(newCharacter.basicMove);
      const response = await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters/${selectedCharacter.id}`, 'put', newCharacter);
      
      setCharacters((prevCharacters) =>
        prevCharacters.map((character) => (character.id === selectedCharacter.id ? response.data : character))
      );
      setNewCharacter({
        photo: '',
        first_name: '',
        last_name: '',
        strength: 0,
        dex: 0,
        intelligence: 0,
        health: 0,
        hitPoints: 0,
        will: 0,
        perception: 0,
        basicSpeed: 0.0,
        basicMove: 0,
        hidden: false,
      });
      setSelectedCharacter(null);
      setError(null);
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

  const selectCharacter = (character) => {
    setSelectedCharacter(character);
    setNewCharacter({
      photo: character.photo,
      first_name: character.first_name,
      last_name: character.last_name,
      strength: character.strength,
      dex: character.dex,
      intelligence: character.intelligence,
      health: character.health,
      hitPoints: character.hitPoints,
      will: character.will,
      perception: character.perception,
      basicSpeed: character.basicSpeed,
      basicMove: character.basicMove,
      hidden: character.hidden,
    });
  };

  const clearSelectedCharacter = () => {
    setSelectedCharacter(null);
    setNewCharacter({
      photo: '',
      first_name: '',
      last_name: '',
      strength: 0,
      dex: 0,
      intelligence: 0,
      health: 0,
      hitPoints: 0,
      will: 0,
      perception: 0,
      basicSpeed: 0.0,
      basicMove: 0,
      hidden: false,
    });
    setError(null);
  };

  const SkillsButton = ({ characterId, campaignId }) => (
    <Link to={`/campaigns/${campaignId}/characters/${characterId}/skills`}>
      <FormButton>
        Skills
      </FormButton>
    </Link>
  );

  const TraitsButton = ({ characterId, campaignId }) => (
    <Link to={`/campaigns/${campaignId}/characters/${characterId}/traits`}>
      <FormButton>
        Traits
      </FormButton>
    </Link>
  );

  const deleteCharacter = async (characterId) => {
    try {
      await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters/${characterId}`, 'delete');
      setCharacters((prevCharacters) => prevCharacters.filter((character) => character.id !== characterId));
      clearSelectedCharacter(); // Clear the selected character after deletion
      setError(null);
    } catch (error) {
      console.error('Error deleting character:', error);
      setError('An error occurred while deleting the character. Please try again.');
    }
  };

  const DeleteButton1 = ({ characterId }) => (
    <DeleteButton type="button" onClick={() => deleteCharacter(characterId)}>
      Delete Character
    </DeleteButton>
  );

  return (
    <CharactersContainer>
      <Header1 />

      <Title><CustomText>Characters</CustomText></Title>

      <Link to="/campaigns">
          <FormButton>
            Back to Campaigns
          </FormButton>
        </Link>

      <CharacterList>
        {characters.map((character) => (
          <CharacterItem key={character.id} onClick={() => selectCharacter(character)}>
            {character.first_name} {character.last_name}
            <EditButtonContainer>
            <SkillsButton characterId={character.id} campaignId={campaignId} />
            <TraitsButton characterId={character.id} campaignId={campaignId} />
            <DeleteButton1 characterId={character.id} />
            </EditButtonContainer>
          </CharacterItem>
        ))}
      </CharacterList>


      <FormContainer>
        <FormTitle>{selectedCharacter ? 'Update Character' : 'Add Character'}</FormTitle>
        {selectedCharacter && (
            <FormButton type="button" onClick={clearSelectedCharacter}>
              New Character
            </FormButton>
            )}
         
        <Form>
        {newCharacter.photo &&
        <ImageContainer>
         <CharacterImage src={newCharacter.photo} alt="Selected Character" 
         />
         </ImageContainer>}
        <FormLabel>
    Photo:
    <FormInput
      type="file"  // Change the input type to "file"
      accept="image/*"  // Optionally specify accepted file types (e.g., images)
      onChange={(e) => handleImageChange(e)}  // Pass the event to handleImageChange
    />
  </FormLabel>

        </Form>
        
        <Form>
          <Table>
          <TableRow>
            <TableCell>
            <Label>
            First Name:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="text"
              value={newCharacter.first_name}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, first_name: e.target.value }))}
            />
            </TableCell>
          </TableRow>
          
          <TableRow>
            <TableCell>
            <Label>
            Last Name:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="text"
              value={newCharacter.last_name}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, last_name: e.target.value }))}
            />
            </TableCell>
          </TableRow>
          
          <TableRow>
            <TableCell>
            <Label>
            Strength:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="number"
              value={newCharacter.strength}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, strength: e.target.value }))}
            />
            </TableCell>
          </TableRow>
          
          <TableRow>
            <TableCell>
            <Label>
            Dexterity:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="number"
              value={newCharacter.dex}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, dex: e.target.value }))}
            />
            </TableCell>
          </TableRow>
         
         <TableRow>
          <TableCell>
          <Label>
            Intelligence:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="number"
              value={newCharacter.intelligence}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, intelligence: e.target.value }))}
            />
            </TableCell>
          
          </TableRow>

          <TableRow>
            <TableCell>
          <Label>
            Health:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="number"
              value={newCharacter.health}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, health: e.target.value }))}
            />
            </TableCell>
            </TableRow>
          
          <TableRow>
            <TableCell>
          <Label>
            Hit Points:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="number"
              value={newCharacter.hitPoints}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, hitPoints: e.target.value }))}
            />
            </TableCell>
          </TableRow>

          <TableRow>
            <TableCell>
          <Label>
            Will:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="number"
              value={newCharacter.will}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, will: e.target.value }))}
            />
          </TableCell>
          </TableRow>

          <TableRow>
            <TableCell>
          <Label>
            Perception:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="number"
              value={newCharacter.perception}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, perception: e.target.value }))}
            />
            </TableCell>
            </TableRow>
          
          <TableRow>
            <TableCell>
          <Label>
            Basic Speed:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="number"
              step="0.1"
              value={newCharacter.basicSpeed}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, basicSpeed: e.target.value }))}
            />
            </TableCell>
          </TableRow>

          <TableRow>
            <TableCell>
          <Label>
            Basic Move:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="number"
              value={newCharacter.basicMove}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, basicMove: e.target.value }))}
            />
            </TableCell>
          </TableRow>

          <TableRow>
            <TableCell>
          <Label>
            Hidden:
            </Label>
            </TableCell>
            <TableCell>
            <FormInput
              type="checkbox"
              checked={newCharacter.hidden}
              onChange={(e) => setNewCharacter((prevCharacter) => ({ ...prevCharacter, hidden: e.target.checked }))}
            />
            </TableCell>
          
          </TableRow>
          </Table>

          <TableRow>
            <TableCell>
          <FormButton type="button" onClick={selectedCharacter ? updateCharacter : addCharacter}>
            {selectedCharacter ? 'Update Character' : 'Add Character'}
          </FormButton>
          </TableCell>
          </TableRow>
          {error && <ErrorMessage>{error}</ErrorMessage>}
         
        </Form>
        
      </FormContainer>
      <Footer1 />
    </CharactersContainer>
  );
};

export default Characters;
