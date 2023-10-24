import React from 'react';
import {RavitButtonComponent} from "../components/RavitButton";
const Home: React.FC = () => {
    return (
        <div className={"contain"}>
            <h1>Hello Home PAge</h1>
            <RavitButtonComponent title={"Hello World"} disable={false}></RavitButtonComponent>
        </div>
    );
}

export default Home;