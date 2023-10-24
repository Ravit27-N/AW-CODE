import React from 'react';


const style = {
    marginLeft:'20px',
    color:'red'
};

const Candidate: React.FC = () => {
    return (
        <div>
            <h1>Hello Candidate PAge</h1>
            <a href={'/candidate/create'}>Create Candidate</a>
            <a style={style} href={'/candidate/createtwo'}>Create not found</a>
        </div>
    );
}



export default Candidate;