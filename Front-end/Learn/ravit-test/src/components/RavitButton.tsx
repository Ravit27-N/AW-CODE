type ButtonSuccess = {
    title: string;
    disable: boolean;
};

function btnClick() {
    alert("Hello world");
}

export const RavitButtonComponent = (props: ButtonSuccess) => {
    const {title, disable} = props;
    return (
        <button disabled={disable} onClick={btnClick}>{title ?? "Ravit"}</button>
    );
}