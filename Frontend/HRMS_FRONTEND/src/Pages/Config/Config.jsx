import React, { useEffect, useState } from "react";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import {
  fetchAllConfiguration,
  updateConfig,
} from "../../Services/ConfigService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { toast } from "react-toastify";

function Config() {
  const { authUser, setAuthUser } = useAuthUserContext();
  const [configurations, SetConfigurations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [id, setId] = useState(0);
  const [key, setKey] = useState(null);
  const [value, setValue] = useState(null);
  useEffect(() => {
    fetchConfigurations();
  }, []);

  const fetchConfigurations = async () => {
    try {
      const res = await fetchAllConfiguration();
      SetConfigurations(res.data);
      console.log(res);
      setLoading(false);
    } catch (e) {
      setLoading(false);
      handleGlobalError(e);
    } finally {
      setLoading(false);
    }
  };
  const onSubmit = async () => {
    try {
      const payload = {
        key: key,
        value: value,
      };
      setLoading(true);
      await updateConfig(parseInt(id), payload);
      toast.success("updated successFully!!");
      setLoading(false);
      setKey(null);
      setValue(null);
      setId(0);
    } catch (e) {
      handleGlobalError(e);
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };
  if (authUser.role != "HR" && authUser.role != "ADMIN") {
    return <p>Not accessible !!!</p>;
  }
  if (!loading && configurations.length == 0) {
    return <p>no configuration stored !!</p>;
  }
  return (
    <div>
      {id != 0 ? (
        <div>
          <div className="w-full bg-gray-100 p-6">
            <div className="max-w-4xl mx-auto space-y-6">
              <div className="bg-white rounded-2xl shadow p-6">
                <div className="grid grid-cols-1">
                  <h2 className="text-2xl text-center font-semibold mb-4">
                    update Cofiguration
                  </h2>
                </div>
                <div className="flex justify-center items-center gap-2">
                <label className="block text-sm font-medium" htmlFor=""> Value of {key} :- </label>
                <input
                className="border rounded px-3 py-2 "
                  type="text"
                  value={value}
                  onChange={(e) => setValue(e.target.value)}
                />
                <button className="ml-3 px-4 py-2 bg-black text-white rounded hover:bg-gray-700" onClick={onSubmit}>Submit</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="w-full bg-gray-100 p-6">
          <div className="max-w-4xl mx-auto space-y-6">
            <div className="bg-white rounded-2xl shadow p-6">
              <div className="grid grid-cols-1">
                <h2 className="text-2xl text-center font-semibold mb-4">
                  Cofiguration
                </h2>
              </div>
              {configurations.length === 0 ? (
                <div className="text-center py-10 text-gray-500">
                  No cofiguration for database
                </div>
              ) : (
                configurations?.map((config) => (
                  <div
                    key={config.id}
                    className="bg-white shadow rounded-lg p-4 border border-gray-200"
                  >
                    <div className="flex-1">
                      <p className="text-sm text-gray-500">Key</p>
                      <p className="font-medium text-gray-800">
                        {config.configKey}
                      </p>
                      <p className="text-sm text-gray-500">Value</p>
                      <p className="font-medium text-gray-800">
                        {config.configValue}
                      </p>
                      <button
                        onClick={() => {
                          setId(parseInt(config.id));
                          setKey(config.configKey);
                        }}
                        className=" bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                      >
                        Update-config
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Config;
