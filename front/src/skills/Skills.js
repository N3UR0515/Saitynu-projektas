import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';
import { useParams, Link } from 'react-router-dom';
import styled from 'styled-components';
import Header1 from '../reuse/Header';
import Footer from '../reuse/Footer';


// Styled components
const SkillsContainer = styled.div`
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

const SkillList = styled.ul`
  list-style: none;
  padding: 0;
`;

const SkillItem = styled.li`
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

const SkillInfo = styled.div`
  flex-grow: 1;
`;

const SkillActions = styled.div``;

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

const FormSelect = styled.select`
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


// Component
const Skills = () => {
  const [skills, setSkills] = useState([]);
  const { campaignId, characterId } = useParams();
  const token = Cookies.get('token');
  const [newSkill, setNewSkill] = useState({
    name: '',
    description: '',
    level: 1,
    difficulty: 'EASY',
    // Add other skill properties here
  });
  const [selectedSkill, setSelectedSkill] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Fetch skills from the backend when the component mounts
    fetchSkills();
  }, []);

  const fetchSkills = async () => {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    try {
      const response = await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters/${characterId}/skills`, 'get');
     
      setSkills(response.data);
    } catch (error) {
      console.error('Error fetching skills:', error);
    }
  };

  const addSkill = async () => {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    try {
      // Send a POST request to add a new skill
      const response = await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters/${characterId}/skills`, 'post', newSkill);
      
      // Optionally, update the local state with the new skill
      setSkills([...skills, response.data]);
      // Clear the form
      setNewSkill({
        name: '',
        description: '',
        level: 1,
        difficulty: 'EASY',
        // Clear other skill properties here
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

  const updateSkill = async () => {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    try {
      // Send a PUT request to update the selected skill
      const response = await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters/${characterId}/skills/${selectedSkill.id}`, 'put', newSkill);
      // Update the local state with the updated skill
      setSkills((prevSkills) =>
        prevSkills.map((skill) => (skill.id === selectedSkill.id ? response.data : skill))
      );
      // Clear the form and reset selected skill
      setNewSkill({
        name: '',
        description: '',
        level: 1,
        difficulty: 'EASY',
        // Clear other skill properties here
      });
      setSelectedSkill(null);
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

  const selectSkill = (skill) => {
    // Set the selected skill for editing
    setSelectedSkill(skill);
    // Populate the form with the selected skill's data
    setNewSkill({
      name: skill.name,
      description: skill.description || '',
      level: skill.level,
      difficulty: skill.difficulty,
      // Populate other skill properties here
    });
    setError(null);
  };

  const deleteSkill = async (skillId) => {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    try {
      // Send a DELETE request to remove the selected skill
      await makeAuthenticatedRequest(`http://localhost:8080/api/campaign/${campaignId}/characters/${characterId}/skills/${skillId}`, 'delete');
      
      // Update the local state by removing the deleted skill
      setSkills((prevSkills) => prevSkills.filter((skill) => skill.id !== skillId));
      // Clear the form and reset selected skill
      setNewSkill({
        name: '',
        description: '',
        level: 1,
        difficulty: 'EASY',
        // Clear other skill properties here
      });
      setSelectedSkill(null);
      setError(null);
    } catch (error) {
      console.error('Error deleting skill:', error);
      setError('An error occurred while deleting the skill. Please try again.');
    }
  };

  const clearForm = () => {
    setSelectedSkill(null);
    setNewSkill({
      name: '',
    description: '',
    level: 1,
    difficulty: 'EASY',
    });
    setError(null);
  };

  return (
    <SkillsContainer>
      <Header1 />
      <Title><CustomText>Skills</CustomText></Title>

      <Link to={`/campaigns/${campaignId}/characters`}>
            <FormButton>
              Back to Character List
            </FormButton>
          </Link>

      {/* Skill list */}
      <SkillList>
        {skills.map((skill) => (
          <SkillItem key={skill.id} onClick={() => selectSkill(skill)}>
            <SkillInfo>
              {skill.name} - Level {skill.level}
              {/* Display other skill properties here */}
            </SkillInfo>
            <SkillActions>
              <DeleteButton onClick={(e) => deleteSkill(skill.id)}>Delete</DeleteButton>
            </SkillActions>
          </SkillItem>
        ))}
      </SkillList>

      {/* Add or Update skill form */}
      <FormContainer>
        <FormTitle>{selectedSkill ? 'Update Skill' : 'Add Skill'}</FormTitle>
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
              value={newSkill.name}
              onChange={(e) => setNewSkill({ ...newSkill, name: e.target.value })}
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
              value={newSkill.description}
              onChange={(e) => setNewSkill({ ...newSkill, description: e.target.value })}
            />
            </TableCell>
            </TableRow>
          <TableRow>
            <TableCell>
          <FormLabel>
            Level:
            </FormLabel>
            </TableCell>
            <TableCell>
            <FormInput
              type="number"
              value={newSkill.level}
              onChange={(e) => setNewSkill({ ...newSkill, level: parseInt(e.target.value, 10) })}
            />
            </TableCell>
            </TableRow>
          <TableRow>
            <TableCell>
          <FormLabel>
            Difficulty:
            </FormLabel>
            </TableCell>
            <TableCell>
            <FormSelect
              value={newSkill.difficulty}
              onChange={(e) => setNewSkill({ ...newSkill, difficulty: e.target.value })}
            >
              <option value="EASY">Easy</option>
              <option value="AVERAGE">Average</option>
              <option value="HARD">Hard</option>
              <option value="VERY HARD">Very Hard</option>
            </FormSelect>
            </TableCell>
            </TableRow>
            </Table>
          
          <TableRow>
            <TableCell>
          <FormButton type="button" onClick={selectedSkill ? updateSkill : addSkill}>
            {selectedSkill ? 'Update Skill' : 'Add Skill'}
          </FormButton>
          </TableCell>
          <TableCell>
          {selectedSkill && (
            <FormButton type="button" onClick={() => clearForm()}>
            New Skill
          </FormButton>
          )}
          </TableCell>
          </TableRow>
          
          {error && <ErrorMessage>{error}</ErrorMessage>}
          
        </Form>
      </FormContainer>
      <Footer />
    </SkillsContainer>
  );
};

export default Skills;
