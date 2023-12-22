import { useEffect, useState } from 'react';
import axios from 'axios';
import Cookies from 'js-cookie';

const Dashboard = () => {
  const [dataFromDemoController, setDataFromDemoController] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Retrieve tokens from cookies
        const token = Cookies.get('token');
        const refreshToken = Cookies.get('refreshToken');

        // Make a request to your API using the saved tokens
        const demoControllerResponse = await axios.get('http://localhost:8080/api/v1/auth/demo-controller', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        // Handle the response from /api/democontroller as needed
        setDataFromDemoController(demoControllerResponse.data);
      } catch (error) {
        console.error('Error fetching data from demoController:', error);
      }
    };

    fetchData();
  }, []);

  return (
    <div>
      <h1>Data from DemoController:</h1>
      {dataFromDemoController && (
        <pre>{JSON.stringify(dataFromDemoController, null, 2)}</pre>
      )}
    </div>
  );
};

export default Dashboard;
