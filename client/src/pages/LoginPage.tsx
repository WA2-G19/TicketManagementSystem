import LoginForm from "../components/loginpage/LoginForm";
import NavbarLayout from "../components/layout/NavbarLayout";

function LoginPage(): JSX.Element {
    return (
        <NavbarLayout>
            <LoginForm/>
        </NavbarLayout>
    )
}

export default LoginPage