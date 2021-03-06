/*
 Copyright (c) 2014 by Contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package ml.dmlc.xgboost4j.scala

import java.io.InputStream

import ml.dmlc.xgboost4j.java.{XGBoost => JXGBoost, XGBoostError}
import scala.collection.JavaConverters._

/**
  * XGBoost Scala Training function.
  */
object XGBoost {
  /**
    * Train a booster given parameters.
    *
    * @param params  Parameters.
    * @param dtrain  Data to be trained.
    * @param round   Number of boosting iterations.
    * @param watches a group of items to be evaluated during training, this allows user to watch
    *                performance on the validation set.
    * @param obj     customized objective
    * @param eval    customized evaluation
    * @return The trained booster.
    */
  @throws(classOf[XGBoostError])
  def train(
      params: Map[String, Any],
      dtrain: DMatrix,
      round: Int,
      watches: Map[String, DMatrix] = Map[String, DMatrix](),
      obj: ObjectiveTrait = null,
      eval: EvalTrait = null): Booster = {


    val jWatches = watches.map{case (name, matrix) => (name, matrix.jDMatrix)}
    val xgboostInJava = JXGBoost.train(
      params.map{
        case (key: String, value) => (key, value.toString)
      }.toMap[String, AnyRef].asJava,
      dtrain.jDMatrix, round, jWatches.asJava,
      obj, eval)
    new Booster(xgboostInJava)
  }

  /**
    * Cross-validation with given parameters.
    *
    * @param params  Booster params.
    * @param data    Data to be trained.
    * @param round   Number of boosting iterations.
    * @param nfold   Number of folds in CV.
    * @param metrics Evaluation metrics to be watched in CV.
    * @param obj     customized objective
    * @param eval    customized evaluation
    * @return evaluation history
    */
  @throws(classOf[XGBoostError])
  def crossValidation(
      params: Map[String, Any],
      data: DMatrix,
      round: Int,
      nfold: Int = 5,
      metrics: Array[String] = null,
      obj: ObjectiveTrait = null,
      eval: EvalTrait = null): Array[String] = {
    JXGBoost.crossValidation(params.map{
      case (key: String, value) => (key, value.toString)
    }.toMap[String, AnyRef].asJava,
      data.jDMatrix, round, nfold, metrics, obj, eval)
  }

  /**
    * load model from modelPath
    *
    * @param modelPath booster modelPath
    */
  @throws(classOf[XGBoostError])
  def loadModel(modelPath: String): Booster = {
    val xgboostInJava = JXGBoost.loadModel(modelPath)
    new Booster(xgboostInJava)
  }

  /**
    * Load a new Booster model from a file opened as input stream.
    * The assumption is the input stream only contains one XGBoost Model.
    * This can be used to load existing booster models saved by other XGBoost bindings.
    *
    * @param in The input stream of the file.
    * @return The create booster
    */
  @throws(classOf[XGBoostError])
  def loadModel(in: InputStream): Booster = {
    val xgboostInJava = JXGBoost.loadModel(in)
    new Booster(xgboostInJava)
  }
}
