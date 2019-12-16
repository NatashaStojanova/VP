import React, {useState, useEffect} from 'react'
import {useParams} from 'react-router-dom';
import axios from '../../../../custom-axios/axios'

const EditIngredient = (props) => {
    const [ingredient, setIngredient] = useState({});
    const {id} = useParams();

    useEffect(() => {
        axios.get("/ingredients/" + id).then((data) => {
            setIngredient(data.data);
        });
    }, []);

    const onFormSubmit = (e) => {
        e.preventDefault();
        props.onSubmit(
            {
                "id": id,
                "name": e.target.ingredientName.value,
                "spicy": e.target.isSpicy.checked,
                "veggie": e.target.isVegie.checked
            }
        );
    };

    const handleTermOnChange = (e) => {
        const paramName = e.target.name;
        const paramValue = e.target.value;
        setIngredient({paramName: paramValue});
    };


    return (
        <div className="row">
            <form className="card" onSubmit={onFormSubmit}>
                <h4 className="text-upper text-left">Edit Ingredient</h4>
                <div className="form-group row">
                    <label htmlFor="ingredient" className="col-sm-4 offset-sm-1 text-left">Name</label>
                    <div className="col-sm-6">
                        <input type="text" onChange={handleTermOnChange} value={ingredient.name}
                               className="form-control" id="ingredient" name={"ingredientName"}
                               placeholder="Ingredient name"/>
                    </div>
                </div>
                <div className="form-group row">
                    <label htmlFor="veggie" className="col-sm-4 offset-sm-1 text-left">Veggie</label>
                    <div className="col-sm-6 col-xl-4">
                        <input type="checkbox" onChange={handleTermOnChange} checked={ingredient.vegie}
                               className="form-control" id="veggie" name={"isVggie"}/>
                    </div>
                </div>
                <div className="form-group row">
                    <label htmlFor="spicy" className="col-sm-4 offset-sm-1 text-left">Spicy</label>
                    <div className="col-sm-6 col-xl-4">
                        <input type="checkbox" onChange={handleTermOnChange} checked={ingredient.spicy}
                               className="form-control" id="spicy" name={"isSpicy"}/>
                    </div>
                </div>
                <div className="form-group row">
                    <div
                        className="offset-sm-1 col-sm-3  text-center">
                        <button
                            type="submit"
                            className="btn btn-primary text-upper">
                            Save
                        </button>
                    </div>
                    <div
                        className="offset-sm-1 col-sm-3  text-center">
                        <button
                            className="btn btn-warning text-upper">
                            Reset
                        </button>
                    </div>
                    <div
                        className="offset-sm-1 col-sm-3  text-center">
                        <button
                            className="btn btn-danger text-upper">
                            Cancel
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
};

export default EditIngredient