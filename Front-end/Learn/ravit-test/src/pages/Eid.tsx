import React from 'react';
import CSS from 'csstype';

const h1Styles: CSS.Properties = {
    backgroundColor: 'rgba(245,231,231,0.85)',
    color: 'grey'
}




const EidPage: React.FC = () => {
    return (
        <div style={h1Styles}>
            <h1>Hello EID PAge</h1>
        </div>
    );
}



export default EidPage;