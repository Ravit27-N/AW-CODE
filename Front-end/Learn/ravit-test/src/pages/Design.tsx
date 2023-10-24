import React from 'react';
import CSS from 'csstype';
import { Button } from '@mui/material';

const h1Styles: CSS.Properties = {
  backgroundColor: 'rgba(245,231,231,0.85)',
  color: 'grey'
}


const designPage: React.FC = () => {
  return (
    <div style={h1Styles}>
      <h1>Hello DESIGN PAGE PAge</h1>
      <Button>TEST</Button>
    </div>
  );
}



export default designPage;