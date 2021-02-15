import React, { useState ,useEffect} from "react";
import {useSelector} from 'react-redux';
import classnames from "classnames";
import { useHistory } from "react-router-dom";
import { login } from "../../actions/securityActions";
import {useStore} from "react-redux";

const Login = ()=>{
    const store =useStore();
    const [loginState,setloginState]=useState({username: "",password: "",errors: {}});
    const security=useSelector(state=>state.security);
    const errors=useSelector(state=>state.errors);
    const history =useHistory();

    useEffect(()=>{
      if (security.validToken) {
        history.push("/dashboard");
      }
      if (errors) {
        setloginState({...loginState,errors:errors });
      }
    },[security,errors]);

    const onChange =(e)=>{
      setloginState({ ...loginState,[e.target.name]: e.target.value });
    }
    const onSubmit =(e)=> {
      e.preventDefault();
      const loginRequest = {
        username: loginState.username,
        password: loginState.password
      };
      console.log(loginRequest);
      store.dispatch(login(loginRequest));
    }
    return (
      <div className="login">
        <div className="container">
          <div className="row">
            <div className="col-md-8 m-auto">
              <h1 className="display-4 text-center">Log In</h1>
              <form onSubmit={onSubmit}>
                <div className="form-group">
                  <input
                    type="text"
                    className={classnames("form-control form-control-lg", {
                      "is-invalid": loginState.errors.username
                    })}
                    placeholder="Email Address"
                    name="username"
                    value={loginState.username}
                    onChange={onChange}
                  />
                  {errors.username && (
                    <div className="invalid-feedback">{loginState.errors.username}</div>
                  )}
                </div>
                <div className="form-group">
                  <input
                    type="password"
                    className={classnames("form-control form-control-lg", {
                      "is-invalid": loginState.errors.password
                    })}
                    placeholder="Password"
                    name="password"
                    value={loginState.password}
                    onChange={onChange}
                  />
                  {loginState.errors.password && (
                    <div className="invalid-feedback">{loginState.errors.password}</div>
                  )}
                </div>
                <input type="submit" className="btn btn-info btn-block mt-4" />
              </form>
            </div>
          </div>
        </div>
      </div>
    );
  }
  export default  Login;